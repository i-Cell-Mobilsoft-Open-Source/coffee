/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.rest.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.container.ContainerRequestContext;

import org.jboss.resteasy.core.ResourceMethodInvoker;

import hu.icellmobilsoft.coffee.cdi.logger.LogProducer;

/**
 * REST Request utils.
 *
 * @author mark.petrenyi
 * @since 1.0.0
 */
public class RequestUtil {

    // see https://download.eclipse.org/microprofile/microprofile-rest-client-1.3/microprofile-rest-client-1.3.html#_clientrequestfilter
    /** Constant <code>ECLIPSE_MICROPROFILE_REST_CLIENT_INVOKED_METHOD_KEY="org.eclipse.microprofile.rest.client.in"{trunked}</code> */
    public static final String ECLIPSE_MICROPROFILE_REST_CLIENT_INVOKED_METHOD_KEY = "org.eclipse.microprofile.rest.client.invokedMethod";

    /**
     * Returns desired {@link Annotation} from given {@link ContainerRequestContext}.
     *
     * @param <A>
     *            type of desired {@code Annotation}
     * @param requestContext
     *            context
     * @param clazz
     *            class of desired {@code Annotation}
     * @return desired {@code Annotation} or null if it is not found
     *
     */
    public static <A extends Annotation> A getAnnotation(ContainerRequestContext requestContext, Class<A> clazz) {
        if (requestContext == null || clazz == null) {
            LogProducer.getStaticDefaultLogger(RequestUtil.class).warn("requestContext or clazz is null!!");
            return null;
        }
        ResourceMethodInvoker invoker = (ResourceMethodInvoker) requestContext.getProperty(ResourceMethodInvoker.class.getName());
        if (invoker == null) {
            // ez nem lehet null soha
            LogProducer.getStaticDefaultLogger(RequestUtil.class).warn("ResourceMethodInvoker is null!!");
            return null;
        }
        Method serviceMethod = invoker.getMethod();
        return serviceMethod.getAnnotation(clazz);
    }

    /**
     * Returns desired {@link Annotation} from given {@link ClientRequestContext}.
     *
     * @param <A>
     *            type of desired {@code Annotation}
     * @param requestContext
     *            context
     * @param clazz
     *            class of desired {@code Annotation}
     * @return desired {@code Annotation} or null if it is not found
     * 
     */
    public static <A extends Annotation> A getAnnotation(ClientRequestContext requestContext, Class<A> clazz) {
        if (requestContext == null || clazz == null) {
            LogProducer.getStaticDefaultLogger(RequestUtil.class).warn("requestContext or clazz is null!!");
            return null;
        }
        Method invokedMethod = (Method) requestContext.getProperty(ECLIPSE_MICROPROFILE_REST_CLIENT_INVOKED_METHOD_KEY);
        if (invokedMethod == null) {
            // ez nem lehet null soha
            LogProducer.getStaticDefaultLogger(RequestUtil.class).warn("invokedMethod is null!!");
            return null;
        }
        return invokedMethod.getAnnotation(clazz);
    }

}
