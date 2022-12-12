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
package hu.icellmobilsoft.coffee.module.redispubsub.bundle;

import java.util.Map;

import org.eclipse.microprofile.reactive.messaging.Message;

/**
 * Redis Pub-Sub implementation of reactive streams {@link Message}. Extends payload with contextual metadata. Redis connector sends the json
 * representation on publish
 *
 * @author mark.petrenyi
 * @since 1.13.0
 */
public class PubSubMessage implements Message<String> {
    private String payload;
    private Map<String, String> context;

    /**
     * Creates PubSubMessage from payload
     *
     * @param payload
     *            the payload
     * @return PubSubMessage instance
     */
    public static PubSubMessage of(String payload) {
        return new PubSubMessage(payload, null);
    }

    /**
     * Creates PubSubMessage from payload and context.
     *
     * @param payload
     *            the payload
     * @param context
     *            the contextual metadata
     * @return PubSubMessage instance
     */
    public static PubSubMessage of(String payload, Map<String, String> context) {
        return new PubSubMessage(payload, context);
    }

    private PubSubMessage(String payload, Map<String, String> context) {
        this.payload = payload;
        this.context = context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPayload() {
        return payload;
    }

    /**
     * Sets message payload.
     *
     * @param payload
     *            the message payload
     */
    public void setPayload(String payload) {
        this.payload = payload;
    }

    /**
     * Gets contextual metadata.
     *
     * @return the contextual metadata
     */
    public Map<String, String> getContext() {
        return context;
    }

    /**
     * Sets contextual metadata.
     *
     * @param context
     *            the contextual metadata
     */
    public void setContext(Map<String, String> context) {
        this.context = context;
    }
}
