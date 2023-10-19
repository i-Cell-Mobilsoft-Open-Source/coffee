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

import jakarta.annotation.Resource;
import jakarta.enterprise.concurrent.ManagedExecutorService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Destroyed;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;

import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * Start/Stop gRPC server
 * 
 * @author czenczl
 * @since 2.1.0
 *
 */
@ApplicationScoped
public class GrpcStarter {

    @Inject
    private Logger log;

    @Resource
    private ManagedExecutorService executorService;

    /**
     * Default constructor, constructs a new object.
     */
    public GrpcStarter() {
        super();
    }

    /**
     * Start gRPC server in unmanaged mode
     * 
     * @param init
     *            observe object
     */
    public void begin(@Observes @Initialized(ApplicationScoped.class) Object init) {
        // run in own thread
        start();
    }

    /**
     * Stop gRPC server
     * 
     * @param payload
     *            observe object
     */
    public void end(@Observes @Destroyed(ApplicationScoped.class) Object payload) {
        stop();
    }

    /**
     * Start gRPC server in unmanaged mode
     */
    protected void start() {
        log.info("Starting grpc server service...");

        IGrpcServerExecutor executor = CDI.current().select(GrpcSampleServerExecutor.class).get();
        executorService.execute(executor);

        log.info("Grpc server started");
    }

    /**
     * Stop all possible gRPC server
     */
    protected void stop() {
        log.info("Stopping grpc server services...");

        Instance<GrpcServerManager> grpcServerManagerInstance = CDI.current().select(GrpcServerManager.class);
        grpcServerManagerInstance.forEach(GrpcServerManager::stopServer);

        log.info("Grpc server stopped");
    }

}
