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
package hu.icellmobilsoft.coffee.grpc.server;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;

/**
 * Sample gRPC server executor
 * 
 * @author czenczl
 * @since 2.1.0
 *
 */
@Dependent
public class GrpcSampleServerExecutor implements IGrpcServerExecutor {

    @Inject
    private GrpcServerManager grpcServerManager;

    /**
     * Default constructor, constructs a new object.
     */
    public GrpcSampleServerExecutor() {
        super();
    }

    @Override
    public void run() {

        try {
            grpcServerManager.init();
        } catch (BaseException e) {
            throw new RuntimeException(e.getLocalizedMessage(), e);
        }
        // own server management implementation, this is being managed under JakartaEE,
        // a sample server management manager similar to ours, until there is no progress in Jakarta
        // https://projects.eclipse.org/projects/ee4j.rpc/reviews/creation-review
        grpcServerManager.startServer();

    }

}
