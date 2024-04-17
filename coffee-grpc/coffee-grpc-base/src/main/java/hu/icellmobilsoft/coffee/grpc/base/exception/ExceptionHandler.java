/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2023 i-Cell Mobilsoft Zrt.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package hu.icellmobilsoft.coffee.grpc.base.exception;

import java.lang.reflect.ParameterizedType;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.CDI;

import org.apache.commons.lang3.reflect.TypeUtils;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.Any;
import com.google.rpc.Code;
import com.google.rpc.ErrorInfo;
import com.google.rpc.Status;

import hu.icellmobilsoft.coffee.se.logging.Logger;
import io.grpc.Metadata;

/**
 * The ExceptionHandler class serves to handle exceptions in gRPC services using ExceptionMappers, similar to how they are used in JAX-RS
 *
 * <p>
 * This class has the capability to handle exceptions using ExceptionMapper instances. It uses CDI to get all available instances of ExceptionMapper,
 * and then filters them by the type of exception they are capable of handling. The selected ExceptionMapper instances are then used to handle the
 * exception and return a corresponding {@link com.google.rpc.Status}.
 * </p>
 *
 * <p>
 * ExceptionMappers with higher priority value (specified via {@link Priority}) are selected first. If no matching ExceptionMapper is found, an
 * {@link com.google.rpc.Code#INTERNAL} error {@link com.google.rpc.Status} is returned.
 * </p>
 *
 * @author mark.petrenyi
 * @see ExceptionMapper
 * @see Priority
 * @see com.google.rpc.Status
 * @since 2.1.0
 */
public class ExceptionHandler {

    private static final Comparator<Bean<?>> PRIORITY_COMPARATOR = Comparator.comparing(Bean::getBeanClass,
            Comparator.comparing(c -> c.getAnnotation(Priority.class), Comparator.nullsLast(Comparator.comparingInt(Priority::value))));
    private Map<Class<? extends Throwable>, List<Bean<?>>> exceptionMapperBeans = new HashMap<>();

    private static final Logger LOG = Logger.getLogger(ExceptionHandler.class);
    private static final ExceptionHandler exceptionHandler = new ExceptionHandler();

    /**
     * Default constructor, constructs a new object.
     */
    public ExceptionHandler() {
        super();
    }

    /**
     * Get exception handler instance
     * 
     * @return {@link ExceptionHandler}
     */
    public static ExceptionHandler getInstance() {
        return exceptionHandler;
    }

    /**
     * Handles the given exception by finding the matching ExceptionMapper instance and using it to get the corresponding {@link StatusResponse}.
     * 
     * @param <E>
     *            Generic type of the exception
     * @param requestHeaders
     *            Grpc request metadata
     * @param t
     *            the exception to be handled
     * @return the corresponding {@link StatusResponse}
     */
    public <E extends Throwable> StatusResponse handle(Metadata requestHeaders, E t) {
        if (t instanceof GrpcRuntimeExceptionWrapper && ((GrpcRuntimeExceptionWrapper) t).getWrapped() != null) {
            return handleStatus(requestHeaders, ((GrpcRuntimeExceptionWrapper) t).getWrapped());
        }
        return handleStatus(requestHeaders, t);
    }

    private <E extends Throwable> StatusResponse handleStatus(Metadata requestHeaders, E t) {
        try {
            List<Bean<?>> mapperBeans = getExceptionMapperBeans(t.getClass());
            for (Bean<?> exceptionMapperBean : mapperBeans) {
                Status status = handleByBean(requestHeaders, t, exceptionMapperBean);
                // ha nem null visszaadjuk, ha null jön priority szerint a következő, esetleg az exception super class-ára írt
                if (status != null) {
                    return StatusResponse.of(status, t);
                }
            }
        } catch (Throwable e) {
            LOG.error("Error occurred in ExceptionHandler - " + e.getMessage(), e);
            return buildInternalErrorStatus(t, e.getMessage());
        }
        String reason = MessageFormat.format("Could not find " + ExceptionMapper.class.getName() + " CDI implementation for [{0}]", t.getClass());
        return buildInternalErrorStatus(t, reason);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private <E extends Throwable> Status handleByBean(Metadata requestHeaders, E t, Bean<?> exceptionMapperBean) {
        Instance<ExceptionMapper> instance = (Instance<ExceptionMapper>) CDI.current().select(exceptionMapperBean.getBeanClass());
        if (instance.isResolvable()) {
            ExceptionMapper exceptionMapper = null;
            try {
                exceptionMapper = instance.get();
                Status status = exceptionMapper.toStatus(requestHeaders, t);
                if (status != null) {
                    return status;
                }
            } catch (Throwable e) {
                LOG.error(MessageFormat.format("Unknown error occurred while calling exceptionMapper of class: [{0}]",
                        exceptionMapperBean.getBeanClass()), e);
            } finally {
                if (exceptionMapper != null && Dependent.class == exceptionMapperBean.getScope()) {
                    instance.destroy(exceptionMapper);
                }
            }
        }
        return null;
    }

    private List<Bean<?>> getExceptionMapperBeans(Class<? extends Throwable> parameterClass) {
        if (!exceptionMapperBeans.containsKey(parameterClass)) {
            putResolvedMappersForParameter(parameterClass);
        }
        return exceptionMapperBeans.getOrDefault(parameterClass, Collections.emptyList());
    }

    private synchronized void putResolvedMappersForParameter(Class<? extends Throwable> parameterClass) {
        if (!exceptionMapperBeans.containsKey(parameterClass)) {
            exceptionMapperBeans.put(parameterClass, resolveParameterizedBeans(ExceptionMapper.class, parameterClass));
        }
    }

    private ImmutableList<Bean<?>> resolveParameterizedBeans(Class<?> beanClass, Class<?> parameterClass) {
        List<Bean<?>> beansFound = new ArrayList<>();
        if (Object.class == parameterClass) {
            return ImmutableList.of();
        }
        ParameterizedType parameterizedType = TypeUtils.parameterize(beanClass, parameterClass);
        Set<Bean<?>> beans = CDI.current().getBeanManager().getBeans(parameterizedType);
        if (beans != null && !beans.isEmpty()) {
            beansFound.addAll(beans.stream().sorted(PRIORITY_COMPARATOR.thenComparing(Bean::getName)).collect(Collectors.toList()));
        }
        // összeszedjük az exception parentjére írt mappereket is
        beansFound.addAll(resolveParameterizedBeans(beanClass, parameterClass.getSuperclass()));
        return ImmutableList.copyOf(beansFound);
    }

    private StatusResponse buildInternalErrorStatus(Throwable throwableNotHandled, String reason) {
        // ha exception mapper elszáll, akkor internal server error.
        Status.Builder statusBuilder = Status.newBuilder();

        // ha nincs ExceptionHandler:
        // 1. INTERNAL status kod
        statusBuilder.setCode(Code.INTERNAL.getNumber());
        // 2. valaszolunk eredeti hibaval
        statusBuilder.setMessage(throwableNotHandled.getMessage());
        // 3. ErrorInfo-ba pakoljuk a reszleteket
        statusBuilder.addDetails(Any.pack(ErrorInfo.newBuilder().setReason(reason).setDomain(throwableNotHandled.getClass().getName()).build()));
        return StatusResponse.of(statusBuilder.build(), throwableNotHandled);
    }
}
