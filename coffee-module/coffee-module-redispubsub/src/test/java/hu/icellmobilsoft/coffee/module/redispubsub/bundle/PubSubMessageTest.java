/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2026 i-Cell Mobilsoft Zrt.
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

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link PubSubMessage} class
 *
 * @author gabor.balazs
 * @since 2.13.0
 */
@DisplayName("PubSubMessage tests")
public class PubSubMessageTest {

    @Test
    public void shouldSuccessfullyDeserialize() {
        String json = """
                {
                  "payload": "test-payload",
                  "context": {
                    "key": "value"
                  }
                }
                """;
        Jsonb jsonb = JsonbBuilder.create();
        PubSubMessage message = jsonb.fromJson(json, PubSubMessage.class);

        Assertions.assertNotNull(message);
        Assertions.assertEquals("test-payload", message.getPayload());
        Assertions.assertNotNull(message.getContext());
        Assertions.assertEquals("value", message.getContext().get("key"));
    }
}
