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
package hu.icellmobilsoft.coffee.module.etcd.extension;

import java.text.MessageFormat;
import java.util.logging.Logger;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import hu.icellmobilsoft.coffee.module.etcd.config.EtcdConfig;

/**
 * ETCD docker runner. Starting single etcd docker for all test class
 * 
 * @author Imre Scheffer
 * @since 2.6.0
 */
public class EtcdDockerRunnerExtension implements BeforeAllCallback, ExtensionContext.Store.CloseableResource {

    private static boolean started = false;

    public static final GenericContainer<?> ETCD_SERVER = new GenericContainer<>(DockerImageName.parse("bitnami/etcd:3.4.29")).withExposedPorts(2379)
            .withEnv("ALLOW_NONE_AUTHENTICATION", "yes").withAccessToHost(true);

    private static Logger log = Logger.getLogger(EtcdDockerRunnerExtension.class.getName());

    @Override
    public void beforeAll(ExtensionContext context) {
        if (!started) {
            log.info("Etcd docker starting...");
            started = true;
            // Your "before all tests" startup logic goes here
            // The following line registers a callback hook when the root test context is shut down
            context.getRoot().getStore(Namespace.GLOBAL).put("ETCD start", this);

            long start = System.currentTimeMillis();
            ETCD_SERVER.start();
            log.info(MessageFormat.format("Etcd docker started in [{0}]ms", System.currentTimeMillis() - start));

            String url = "http://" + ETCD_SERVER.getHost() + ":" + ETCD_SERVER.getMappedPort(2379);
            System.setProperty(EtcdConfig.URL_KEY, url);
        }
    }

    @Override
    public void close() {
        // Your "after all tests" logic goes here
        long start = System.currentTimeMillis();
        log.info("Etcd docker stopping...");
        ETCD_SERVER.close();
        log.info(MessageFormat.format("Etcd docker stopped in [{0}]ms", System.currentTimeMillis() - start));
    }
}
