/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2024 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.grpc.client;

import java.text.MessageFormat;
import java.util.function.Function;

import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.grpc.client.exception.GrpcClienResponseException;
import io.grpc.StatusRuntimeException;

/**
 * Grpc client call wrapper, handling common problems
 * 
 * @author Imre Scheffer
 * @since 2.7.0
 */
public class GrpcClientWrapper {

    /**
     * Default constructor, constructs a new object.
     */
    private GrpcClientWrapper() {
        super();
    }

    /**
     * Wrap and call given function. Main focus is standardized handling exception and other common usage. Example usage:
     * 
     * <pre>
     * &#64;Inject
     * &#64;GrpcClient(configKey = "key")
     * private GeneratedServiceGrpc.SampleBlockingStub blockingStub;
     *
     * // customize header
     * GeneratedServiceGrpc.SampleBlockingStub stub = GrpcHeaderHelper.addHeader(blockingStub, GrpcHeaderHelper.headerWithSid(errorLanguage));
     * // call function
     * return GrpcClientWrapper.call(stub::uploadDocument, requestObject);
     * </pre>
     * 
     * @param <ReqT>
     *            Grpc Request type
     * @param <RespT>
     *            Grpc Response type
     * @param stubFunction
     *            Function to call Grpc callable method
     * @param request
     *            Request object to send
     * @return Response object from send
     * @throws GrpcClienResponseException
     *             If any standard or expected error occured, transformed to this
     * @throws TechnicalException
     *             If not expected, technology error occured
     */
    public static <ReqT, RespT> RespT call(Function<ReqT, RespT> stubFunction, ReqT request) throws GrpcClienResponseException, TechnicalException {
        try {
            return stubFunction.apply(request);
        } catch (StatusRuntimeException e) {
            GrpcClienResponseException responseException = GrpcClienResponseException.fromGrpcResponseException(e);
            throw responseException;
        } catch (Exception e) {
            String msg = MessageFormat.format("Exception occured on GRPC call: [{0}]", e.getLocalizedMessage());
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, msg, e);
        }
    }
}
