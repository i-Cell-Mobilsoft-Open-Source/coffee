/*-
 * #%L
 * Sampler
 * %%
 * Copyright (C) 2022 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.module.redispubsub;

import org.eclipse.microprofile.config.Config;

/**
 * Extract the incoming configuration for the {@code coffee-redis-pubsub} connector. <br>
 * PM: It would be good to generate using ConnectorAttribute annotation, but it's still experimental under WF, so for now, copy-paste and fix the javadoc in the generated code.
 * 
 * @author mark.petrenyi
 * @since 1.13.0
 */
public class PubSubConnectorIncomingConfiguration extends PubSubConnectorCommonConfiguration {

    /**
     * Creates a new PubSubConnectorIncomingConfiguration.
     *
     * @param config
     *            the connector configuration
     */
    public PubSubConnectorIncomingConfiguration(Config config) {
        super(config);
        validate();
    }

    /**
     * Gets the retry-seconds value from the configuration. Attribute Name: retry-seconds Description: Number of seconds to wait after subscription
     * failure, before attempting retry Default Value: 30
     * 
     * @return the retry-seconds
     */
    public Integer getRetrySeconds() {
        return config.getOptionalValue("retry-seconds", Integer.class).orElse(30);
    }

    public void validate() {
        super.validate();
    }
}
