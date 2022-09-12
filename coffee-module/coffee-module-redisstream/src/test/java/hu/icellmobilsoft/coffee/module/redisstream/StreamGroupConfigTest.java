/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2022 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.module.redisstream;

import hu.icellmobilsoft.coffee.module.redisstream.config.StreamGroupConfig;

import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 * Test for StreamGroupConfig class
 *
 * @author peter.kovacs
 * @since 1.11.0
 */
@EnableWeld
@Tag("weld")
@ExtendWith(WeldJunit5Extension.class)
@DisplayName("Redis StreamGroupConfig test")
class StreamGroupConfigTest {

    public static final String CONFIG_KEY = "streamconfig1";
    @Inject
    private StreamGroupConfig streamGroupConfig;

    @WeldSetup
    public WeldInitiator weld = WeldInitiator.from(WeldInitiator.createWeld()
            // beans.xml scan
            .enableDiscovery())
            // start request scope + build
            .activate(RequestScoped.class).build();

    @Test
    @DisplayName("redis1 StreamGroupConfig test")
    void redis1Test() {
        streamGroupConfig.setConfigKey(CONFIG_KEY);
        Assertions.assertEquals(false, streamGroupConfig.isEnabled());
        Assertions.assertEquals(8000, streamGroupConfig.getStreamReadTimeoutMillis());
        Assertions.assertEquals("redis1", streamGroupConfig.getConnectionKey());
        Assertions.assertEquals("default", streamGroupConfig.getProducerPool());
        Assertions.assertEquals(3600000, streamGroupConfig.getProducerTTL().get());
        Assertions.assertEquals(10000, streamGroupConfig.getProducerMaxLen().get());
        Assertions.assertEquals("custom1", streamGroupConfig.getConsumerPool());
        Assertions.assertEquals(2, streamGroupConfig.getRetryCount().get());
        Assertions.assertEquals(2, streamGroupConfig.getConsumerThreadsCount().get());

    }

}
