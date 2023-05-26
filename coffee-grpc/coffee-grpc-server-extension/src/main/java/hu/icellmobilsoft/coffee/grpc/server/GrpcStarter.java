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

import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Destroyed;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;

import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * Start/Stop gRPC server
 * 
 * @author czenczl
 * @since 1.14.0
 *
 */
@ApplicationScoped
public class GrpcStarter {

    @Inject
    private Logger log;

    @Resource
    private ManagedExecutorService executorService;

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
     * Start gRPC server in unmanaged mode
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
     * Stop all gRPC server
     */
    protected void stop() {
        log.info("Stopping grpc server services...");

        Instance<GrpcServerManager> grpcServerManagerInstance = CDI.current().select(GrpcServerManager.class);
        grpcServerManagerInstance.forEach(GrpcServerManager::stopServer);

        log.info("Grpc server stopped");
    }

}
