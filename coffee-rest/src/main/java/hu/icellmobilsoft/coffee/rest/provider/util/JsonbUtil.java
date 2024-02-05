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
package hu.icellmobilsoft.coffee.rest.provider.util;

import java.text.MessageFormat;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.config.BinaryDataStrategy;
import jakarta.json.bind.config.PropertyVisibilityStrategy;

import org.eclipse.microprofile.config.Config;

import hu.icellmobilsoft.coffee.rest.provider.FieldOnlyVisibilityStrategy;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.coffee.tool.utils.config.ConfigUtil;

/**
 * {@link Jsonb} object creator util class.
 * <code>
 * coffee:
 *   jsonb:
 *     config:
 *       propertyvisibilitystrategyclass: "hu.icellmobilsoft.coffee.rest.provider.FieldOnlyVisibilityStrategy"
 *       binarydatastrategy: "BASE_64"
 * </code>
 * @author speter555
 * @since 2.5.0
 */
public class JsonbUtil {

    /**
     * Config delimiter
     */
    private static final String KEY_DELIMITER = ".";

    private static final String PROPERTYVISIBILITYSTRATEGYCLASS_POSTFIX = "propertyvisibilitystrategyclass";

    private static final String BINARY_DATA_STRATEGY_POSTFIX = "binarydatastrategy";
    /**
     * Prefix for all configs
     */
    public static final String JSONB_CONFIG_PREFIX = "coffee.jsonb.config";

    /**
     * Create {@link Jsonb} instance with {@link FieldOnlyVisibilityStrategy} property visibility strategy.
     * 
     * @return configured {@link Jsonb} instance
     */
    public static Jsonb getContext() {
        Config config = ConfigUtil.getInstance().defaultConfig();
        JsonbConfig jsonbConfig = new JsonbConfig()
                // property visibility strategy setting
                .withPropertyVisibilityStrategy(getPropertyVisibilityStrategyClass(config))
                .withBinaryDataStrategy(getBinaryDataStrategy(config));
        return JsonbBuilder.newBuilder().withConfig(jsonbConfig).build();
    }

    protected static PropertyVisibilityStrategy getPropertyVisibilityStrategyClass(Config config) {
        String className = config.getOptionalValue(joinKey(PROPERTYVISIBILITYSTRATEGYCLASS_POSTFIX), String.class)
                .orElse("hu.icellmobilsoft.coffee.rest.provider.FieldOnlyVisibilityStrategy");
        try {
            return (PropertyVisibilityStrategy) Class.forName(className).getConstructor().newInstance();
        } catch (Exception e) {
            Logger.getLogger(JsonbUtil.class)
                    .warn(
                            MessageFormat.format(
                                    "The PropertyVisibilityStrategy class in the [{0}] config with value [{1}] has a problem ",
                                    joinKey(PROPERTYVISIBILITYSTRATEGYCLASS_POSTFIX),
                                    className),
                            e);
            return new FieldOnlyVisibilityStrategy();
        }
    }

    protected static String getBinaryDataStrategy(Config config) {
        return config.getOptionalValue(joinKey(BINARY_DATA_STRATEGY_POSTFIX), String.class).orElse(BinaryDataStrategy.BASE_64);
    }

    protected static String joinKey(String key) {
        return String.join(KEY_DELIMITER, JSONB_CONFIG_PREFIX, key);
    }

    /**
     * Private constructor
     */
    private JsonbUtil() {
        super();
    }
}
