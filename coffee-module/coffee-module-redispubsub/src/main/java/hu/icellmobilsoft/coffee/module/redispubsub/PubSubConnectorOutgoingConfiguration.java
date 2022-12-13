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
 * Extract the outgoing configuration for the {@code coffee-redis-pubsub} connector. <br>
 * PM: Jó lenne ConnectorAttribute annotációval generáltatni, de wf alatt még experimental, ezért copy-paste és javadoc fix a generátumból
 *
 * @author mark.petrenyi
 * @since 1.13.0
 */
public class PubSubConnectorOutgoingConfiguration extends PubSubConnectorCommonConfiguration {

    /**
     * Creates a new PubSubConnectorOutgoingConfiguration.
     *
     * @param config
     *            the connector configuration
     */
    public PubSubConnectorOutgoingConfiguration(Config config) {
        super(config);
        validate();
    }

    public void validate() {
        super.validate();
    }
}
