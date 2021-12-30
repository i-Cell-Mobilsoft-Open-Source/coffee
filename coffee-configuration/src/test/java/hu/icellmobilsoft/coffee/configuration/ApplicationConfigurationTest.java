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
package hu.icellmobilsoft.coffee.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import hu.icellmobilsoft.coffee.cdi.logger.AppLoggerImpl;
import hu.icellmobilsoft.coffee.cdi.logger.LogContainer;
import hu.icellmobilsoft.coffee.cdi.logger.LogProducer;
import hu.icellmobilsoft.coffee.tool.utils.string.RandomUtil;

import io.smallrye.config.inject.ConfigExtension;

/**
 * 
 * ApplicationConfiguration test class
 * 
 * @author tamas.cserhati
 *
 */
@DisplayName("ApplicationConfiguration class tests")
@EnableWeld
@Tag("weld")
@ExtendWith(WeldJunit5Extension.class)
class ApplicationConfigurationTest {

    @Inject
    private ApplicationConfiguration applicationConfiguration;

    private static final String TEST_CONFIG_KEY = "etcd.sample.key";

    @SuppressWarnings("unchecked")
    @WeldSetup
    public WeldInitiator weld = WeldInitiator
            .from(WeldInitiator.createWeld().addExtensions(ConfigExtension.class).addBeanClasses(LogContainer.class, AppLoggerImpl.class,
                    LogProducer.class, ApplicationConfiguration.class, ConfigurationHelper.class))
            .activate(RequestScoped.class).build();

    @Test
    @DisplayName("Test get existing optional value")
    void getExistingOptionalValue() {
        String value = RandomUtil.generateId();
        System.setProperty(TEST_CONFIG_KEY, value);
        assertEquals(value, applicationConfiguration.getOptionalValue(TEST_CONFIG_KEY, String.class).get());
    }

    @Test
    @DisplayName("Test get non-existing optional value")
    void getNonExistingOptionalValue() {
        applicationConfiguration.clear();
        assertEquals(Optional.empty(), applicationConfiguration.getOptionalValue(TEST_CONFIG_KEY, String.class));
    }

    @Test
    @DisplayName("Test get existing value")
    void getExistingValue() {
        String value = RandomUtil.generateId();
        System.setProperty(TEST_CONFIG_KEY, value);
        assertEquals(value, applicationConfiguration.getValue(TEST_CONFIG_KEY, String.class));
    }

    @Test
    @DisplayName("Test get non-existing value")
    void getNonExistingValue() {
        applicationConfiguration.clear();
        assertEquals(null, applicationConfiguration.getValue(TEST_CONFIG_KEY, String.class));
    }

}
