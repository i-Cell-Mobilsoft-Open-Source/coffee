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
package hu.icellmobilsoft.coffee.tool.utils.json;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import org.eclipse.microprofile.config.Config;

import hu.icellmobilsoft.coffee.tool.utils.config.ConfigUtil;

/**
 * {@link Jsonb} object creator util class.
 *
 * <pre>
 * coffee:
 *   jsonb:
 *     config:
 *       {@value JsonbConfigBuilder#PROPERTY_VISIBILITY_STRATEGY_CLASS}: "hu.icellmobilsoft.coffee.rest.provider.FieldOnlyVisibilityStrategy"
 *       {@value JsonbConfigBuilder#BINARY_DATA_STRATEGY}: "BASE_64"
 *       {@value JsonbConfigBuilder#NULL_VALUES}: false
 *       {@value JsonbConfigBuilder#FORMATTING}: false
 *       {@value JsonbConfigBuilder#FAIL_ON_UNKNOWN_PROPERTIES}: false
 *       {@value JsonbConfigBuilder#ENCODING}: "UTF-8"
 *       {@value JsonbConfigBuilder#STRICT_IJSON}: false
 *       {@value JsonbConfigBuilder#PROPERTY_NAMING_STRATEGY}: IDENTITY
 *       {@value JsonbConfigBuilder#PROPERTY_ORDER_STRATEGY}: LEXICOGRAPHICAL
 *       {@value JsonbConfigBuilder#DATE_FORMAT}: "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
 *       {@value JsonbConfigBuilder#LOCALE}: "en_US"
 *       {@value JsonbConfigBuilder#CUSTOM_ADAPTERS}:
 *          - "your.custom.JsonbAdapter1"
 *          - "your.custom.JsonbAdapter2"
 *       {@value JsonbConfigBuilder#CUSTOM_SERIALIZERS}:
 *          - "your.custom.JsonbSerializer1"
 *          - "your.custom.JsonbSerializer2"
 *       {@value JsonbConfigBuilder#CUSTOM_DESERIALIZERS}:
 *          - "your.custom.JsonbDeserializer1"
 *          - "your.custom.JsonbDeserializer2"
 *       {@value JsonbConfigBuilder#CUSTOM_PROPERTIES}:
 *          - "jsonb.other-config-parameter1#value1"
 *          - "jsonb.other-config-parameter2#value2"
 *
 * </pre>
 *
 * @author speter555
 * @author bucherarnold
 * @since 2.6.0
 */
public class JsonbUtil {

    /**
     * Private constructor
     */
    private JsonbUtil() {
        super();
    }

    /**
     * Create {@link Jsonb} instance with configurable property fields
     *
     * @return configured {@link Jsonb} instance
     */
    public static Jsonb getContext() {
        JsonbConfigBuilder jsonbConfigBuilder = new JsonbConfigBuilder();
        return getContext(jsonbConfigBuilder);
    }

    /**
     * Creates a new {@link Jsonb} instance using the provided {@link JsonbConfigBuilder}.
     *
     * @param jsonbConfigBuilder
     *            the configuration builder used to construct {@link JsonbConfig}
     * @return a configured {@link Jsonb} instance
     */
    public static Jsonb getContext(JsonbConfigBuilder jsonbConfigBuilder) {
        Config config = ConfigUtil.getInstance().defaultConfig();
        JsonbConfig jsonbConfig = jsonbConfigBuilder.withConfig(config).build();

        return JsonbBuilder.newBuilder().withConfig(jsonbConfig).build();
    }

}
