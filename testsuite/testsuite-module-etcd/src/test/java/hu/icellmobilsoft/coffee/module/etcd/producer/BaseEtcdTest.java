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
package hu.icellmobilsoft.coffee.module.etcd.producer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import jakarta.enterprise.context.RequestScoped;

import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;

import hu.icellmobilsoft.coffee.cdi.logger.AppLoggerImpl;
import hu.icellmobilsoft.coffee.cdi.logger.LogContainer;
import hu.icellmobilsoft.coffee.cdi.logger.LogProducer;
import hu.icellmobilsoft.coffee.module.etcd.config.DefaultEtcdConfigImpl;
import hu.icellmobilsoft.coffee.module.etcd.extension.EtcdDockerRunnerExtension;
import hu.icellmobilsoft.coffee.module.etcd.handler.ConfigEtcdHandler;
import io.smallrye.config.inject.ConfigExtension;

/**
 * Base class for ETCD tests
 * 
 * @author Imre Scheffer
 * @since 2.6.0
 */
@EnableWeld
@Tag("weld")
@ExtendWith(WeldJunit5Extension.class)
@ExtendWith(EtcdDockerRunnerExtension.class)
public class BaseEtcdTest {

    static final String NO_VALUE = "TEST_VALUE_NONE";

    static List<String> ALL_ETCD_CONFIG_SOURCE = Arrays.asList(DefaultEtcdConfigSource.class.getName(), CachedEtcdConfigSource.class.getName(),
            RuntimeEtcdConfigSource.class.getName());

    @SuppressWarnings("unchecked")
    @WeldSetup
    WeldInitiator weld = WeldInitiator
            // .from(WeldInitiator.createWeld().enableDiscovery())
            .from(WeldInitiator.createWeld()
                    // smallrye mp config
                    .addExtensions(ConfigExtension.class)
                    // coffee logging
                    .addBeanClasses(LogContainer.class, AppLoggerImpl.class, LogProducer.class)
                    // coffee etcd modul
                    .addBeanClasses(DefaultEtcdFactory.class, DefaultEtcdConfigImpl.class, ConfigEtcdHandler.class))
            .activate(RequestScoped.class).build();

    @BeforeAll
    static void defaultBeforeAll() {
    }

    @AfterAll
    static void defaultAfterAll() {
    }

    void assertAllEtcdConfigSource() {
        List<String> csList = new ArrayList<>();
        Iterator<ConfigSource> it = ConfigProvider.getConfig().getConfigSources().iterator();
        it.forEachRemaining(cs -> csList.add(cs.getClass().getName()));
        Assertions.assertTrue(csList.containsAll(ALL_ETCD_CONFIG_SOURCE),
                "Current config settings not contains all coffee ConfigSources\nmust contain:" + ALL_ETCD_CONFIG_SOURCE + "\ncurrent:" + csList);
    }
}
