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

import java.text.MessageFormat;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.config.BinaryDataStrategy;
import jakarta.json.bind.config.PropertyVisibilityStrategy;

import org.eclipse.microprofile.config.Config;

import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.coffee.tool.jsonb.FieldOnlyVisibilityStrategy;
import hu.icellmobilsoft.coffee.tool.jsonb.adapter.ByteArrayJsonbAdapter;
import hu.icellmobilsoft.coffee.tool.jsonb.adapter.YearMonthJsonbAdapter;
import hu.icellmobilsoft.coffee.tool.utils.config.ConfigUtil;

/**
 * {@link Jsonb} object creator util class.
 *
 * <pre>
 * coffee:
 *   jsonb:
 *     config:
 *       {@value JsonbUtil#PROPERTY_VISIBILITY_STRATEGY_CLASS}: "hu.icellmobilsoft.coffee.rest.provider.FieldOnlyVisibilityStrategy"
 *       {@value JsonbUtil#BINARY_DATA_STRATEGY}: "BASE_64"
 *       {@value JsonbUtil#NULL_VALUES}: false
 *       {@value JsonbUtil#FORMATTING}: false
 *       {@value JsonbUtil#FAIL_ON_UNKNOWN_PROPERTIES}: false
 * </pre>
 *
 * @author speter555
 * @author bucherarnold
 * @since 2.6.0
 */
public class JsonbUtil {

    private static final String MSG_WARN_PROPERTY_VISIBILITY_STRATEGY = "The PropertyVisibilityStrategy class in the [{0}] config with value [{1}] has a problem ";

    /**
     * Config delimiter
     */
    private static final String KEY_DELIMITER = ".";
    /**
     * Prefix for all configs
     */
    public static final String JSONB_CONFIG_PREFIX = "coffee.jsonb.config" + KEY_DELIMITER;
    /**
     * Config property for jsonb.property-visibility-strategy
     */
    private static final String PROPERTY_VISIBILITY_STRATEGY_CLASS = JSONB_CONFIG_PREFIX + "propertyVisibilityStrategyClass";
    /**
     * Config property for jsonb.binary-data-strategy
     */
    private static final String BINARY_DATA_STRATEGY = JSONB_CONFIG_PREFIX + "binaryDataStrategy";
    /**
     * Config property for jsonb.null-values
     */
    private static final String NULL_VALUES = JSONB_CONFIG_PREFIX + "nullValues";
    /**
     * Config property for jsonb.formatting
     */
    private static final String FORMATTING = JSONB_CONFIG_PREFIX + "formatting";
    /**
     * Config property for jsonb.fail-on-unknown-properties
     */
    private static final String FAIL_ON_UNKNOWN_PROPERTIES = JSONB_CONFIG_PREFIX + "failOnUnknownProperties";
    /**
     * Config property name
     */
    private static final String JSONB_PROPERTY_NAME_FAIL_ON_UNKNOWN_PROPERTIES = "jsonb.fail-on-unknown-properties";

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
        Config config = ConfigUtil.getInstance().defaultConfig();
        JsonbConfig jsonbConfig = new JsonbConfig().withPropertyVisibilityStrategy(getPropertyVisibilityStrategyClass(config))
                .withBinaryDataStrategy(getBinaryDataStrategy(config))
                .withNullValues(getSerializeNullValues(config))
                .withFormatting(getFormatting(config))
                .withAdapters(new YearMonthJsonbAdapter(), new ByteArrayJsonbAdapter());
        jsonbConfig.setProperty(JSONB_PROPERTY_NAME_FAIL_ON_UNKNOWN_PROPERTIES, getFailOnUnknownProperties(config));
        return JsonbBuilder.newBuilder().withConfig(jsonbConfig).build();
    }

    private static PropertyVisibilityStrategy getPropertyVisibilityStrategyClass(Config config) {
        String className = config.getOptionalValue(PROPERTY_VISIBILITY_STRATEGY_CLASS, String.class)
                .orElse("hu.icellmobilsoft.coffee.tool.jsonb.FieldOnlyVisibilityStrategy");
        try {
            return (PropertyVisibilityStrategy) Class.forName(className).getConstructor().newInstance();
        } catch (Exception e) {
            String msg = MessageFormat.format(MSG_WARN_PROPERTY_VISIBILITY_STRATEGY, PROPERTY_VISIBILITY_STRATEGY_CLASS, className);
            Logger.getLogger(JsonbUtil.class).warn(msg, e);
            return new FieldOnlyVisibilityStrategy();
        }
    }

    private static String getBinaryDataStrategy(Config config) {
        return config.getOptionalValue(BINARY_DATA_STRATEGY, String.class).orElse(BinaryDataStrategy.BASE_64);
    }

    private static boolean getSerializeNullValues(Config config) {
        return config.getOptionalValue(NULL_VALUES, Boolean.class).orElse(false);
    }

    private static boolean getFormatting(Config config) {
        return config.getOptionalValue(FORMATTING, Boolean.class).orElse(false);
    }

    private static boolean getFailOnUnknownProperties(Config config) {
        return config.getOptionalValue(FAIL_ON_UNKNOWN_PROPERTIES, Boolean.class).orElse(false);
    }

}
