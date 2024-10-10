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

import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.adapter.JsonbAdapter;
import jakarta.json.bind.config.BinaryDataStrategy;
import jakarta.json.bind.config.PropertyNamingStrategy;
import jakarta.json.bind.config.PropertyOrderStrategy;
import jakarta.json.bind.config.PropertyVisibilityStrategy;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.bind.serializer.JsonbSerializer;

import org.apache.commons.lang3.BooleanUtils;
import org.eclipse.microprofile.config.Config;

import hu.icellmobilsoft.coffee.tool.jsonb.adapter.ByteArrayJsonbAdapter;
import hu.icellmobilsoft.coffee.tool.jsonb.adapter.ClassTypeJsonbAdapter;
import hu.icellmobilsoft.coffee.tool.jsonb.adapter.DateJsonbAdapter;
import hu.icellmobilsoft.coffee.tool.jsonb.adapter.DurationJsonbAdapter;
import hu.icellmobilsoft.coffee.tool.jsonb.adapter.XMLGregorianCalendarJsonbAdapter;
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
 *       {@value JsonbUtil#ENCODING}: "UTF-8"
 *       {@value JsonbUtil#STRICT_IJSON}: false
 *       {@value JsonbUtil#PROPERTY_NAMING_STRATEGY}: IDENTITY
 *       {@value JsonbUtil#PROPERTY_ORDER_STRATEGY}: LEXICOGRAPHICAL
 *       {@value JsonbUtil#DATE_FORMAT}: "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
 *       {@value JsonbUtil#LOCALE}: "en_US"
 *       {@value JsonbUtil#CUSTOM_ADAPTERS}:
 *          - "your.custom.JsonbAdapter1"
 *          - "your.custom.JsonbAdapter2"
 *       {@value JsonbUtil#CUSTOM_SERIALIZERS}:
 *          - "your.custom.JsonbSerializer1"
 *          - "your.custom.JsonbSerializer2"
 *       {@value JsonbUtil#CUSTOM_DESERIALIZERS}:
 *          - "your.custom.JsonbDeserializer1"
 *          - "your.custom.JsonbDeserializer2"
 *       {@value JsonbUtil#CUSTOM_PROPERTIES}:
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

    private static final String DEFAULT_FIELD_ONLY_VISIBILITY_STRATEGY = "hu.icellmobilsoft.coffee.tool.jsonb.FieldOnlyVisibilityStrategy";

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
     * Config property for jsonb.encoding
     */
    private static final String ENCODING = JSONB_CONFIG_PREFIX + "encoding";
    /**
     * Config property for jsonb.strict-i-json
     */
    private static final String STRICT_IJSON = JSONB_CONFIG_PREFIX + "strictIJSON";
    /**
     * Config property for jsonb.property-naming-strategy
     */
    private static final String PROPERTY_NAMING_STRATEGY = JSONB_CONFIG_PREFIX + "propertyNamingStrategy";
    /**
     * Config property for jsonb.property-order-strategy
     */
    private static final String PROPERTY_ORDER_STRATEGY = JSONB_CONFIG_PREFIX + "propertyOrderStrategy";
    /**
     * Config property for jsonb.date-format
     */
    private static final String DATE_FORMAT = JSONB_CONFIG_PREFIX + "dateFormat";
    /**
     * Config property for jsonb.locale
     */
    private static final String LOCALE = JSONB_CONFIG_PREFIX + "locale";
    /**
     * Config property for custom adapter classes
     */
    private static final String CUSTOM_ADAPTERS = JSONB_CONFIG_PREFIX + "customAdapters";
    /**
     * Config property for custom serializer classes
     */
    private static final String CUSTOM_SERIALIZERS = JSONB_CONFIG_PREFIX + "customSerializers";
    /**
     * Config property for custom deserializers classes
     */
    private static final String CUSTOM_DESERIALIZERS = JSONB_CONFIG_PREFIX + "customDeserializers";
    /**
     * Config property for custom properties values
     */
    private static final String CUSTOM_PROPERTIES = JSONB_CONFIG_PREFIX + "customProperties";

    /**
     * Config property names
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
        JsonbConfig jsonbConfig = new JsonbConfig()//
                .withPropertyVisibilityStrategy(getPropertyVisibilityStrategyClass(config))
                .withBinaryDataStrategy(getBinaryDataStrategy(config))
                .withNullValues(getSerializeNullValues(config))
                .withFormatting(getFormatting(config))
                .withEncoding(getEncoding(config))
                .withStrictIJSON(getStrictIJson(config))
                .withPropertyNamingStrategy(getPropertyNamingStrategy(config))
                .withPropertyOrderStrategy(getPropertyOrderStrategy(config))
                .withDateFormat(getDateFormat(config), getLocale(config))
                .withLocale(getLocale(config))
                .withAdapters(getAdapters(config))
                .withSerializers(getSerializers(config))
                .withDeserializers(getDeserializers(config));

        // add custom property configurations from config
        getCustomProperties(config).forEach(jsonbConfig::setProperty);

        return JsonbBuilder.newBuilder().withConfig(jsonbConfig).build();
    }

    @SuppressWarnings("rawtypes")
    private static JsonbAdapter[] getAdapters(Config config) {
        List<JsonbAdapter> jsonbAdapters = new ArrayList<>();
        jsonbAdapters.add(new YearMonthJsonbAdapter());
        jsonbAdapters.add(new ByteArrayJsonbAdapter());
        jsonbAdapters.add(new ClassTypeJsonbAdapter());
        jsonbAdapters.add(new DateJsonbAdapter());
        jsonbAdapters.add(new DurationJsonbAdapter());
        jsonbAdapters.add(new XMLGregorianCalendarJsonbAdapter());

        Optional<List<String>> customClassNames = config.getOptionalValues(CUSTOM_ADAPTERS, String.class);
        if (customClassNames.isPresent()) {
            // add custom adapters from config
            for (String customAdapterClass : customClassNames.get()) {
                jsonbAdapters.add(getCustomClassInstance(customAdapterClass));
            }
        }

        return jsonbAdapters.toArray(new JsonbAdapter[0]);
    }

    @SuppressWarnings("rawtypes")
    private static JsonbSerializer[] getSerializers(Config config) {
        List<JsonbSerializer> jsonbSerializers = new ArrayList<>();
        Optional<List<String>> customClassNames = config.getOptionalValues(CUSTOM_SERIALIZERS, String.class);
        if (customClassNames.isPresent()) {
            // add custom serializers from config
            for (String customAdapterClass : customClassNames.get()) {
                jsonbSerializers.add(getCustomClassInstance(customAdapterClass));
            }
        }
        return jsonbSerializers.toArray(new JsonbSerializer[0]);
    }

    @SuppressWarnings("rawtypes")
    private static JsonbDeserializer[] getDeserializers(Config config) {
        List<JsonbDeserializer> jsonbDeserializers = new ArrayList<>();
        Optional<List<String>> customClassNames = config.getOptionalValues(CUSTOM_DESERIALIZERS, String.class);
        if (customClassNames.isPresent()) {
            // add custom serializers from config
            for (String customAdapterClass : customClassNames.get()) {
                jsonbDeserializers.add(getCustomClassInstance(customAdapterClass));
            }
        }
        return jsonbDeserializers.toArray(new JsonbDeserializer[0]);
    }

    private static Map<String, Object> getCustomProperties(Config config) {
        Map<String, Object> customProperties = new HashMap<>();

        // add default custom property
        customProperties.put(JSONB_PROPERTY_NAME_FAIL_ON_UNKNOWN_PROPERTIES, getFailOnUnknownProperties(config));

        Optional<List<String>> customPropertiesConfig = config.getOptionalValues(CUSTOM_PROPERTIES, String.class);
        if (customPropertiesConfig.isPresent()) {
            // add custom property from config
            for (String customProperty : customPropertiesConfig.get()) {
                String[] configValue = customProperty.split("#");
                String key = configValue[0];
                String value = configValue[1];
                if (BooleanUtils.toBooleanObject(value) != null) {
                    // jsonb requires to pass boolean as config value
                    customProperties.put(key, BooleanUtils.toBoolean(value));
                } else {
                    customProperties.put(key, value);
                }
            }
        }

        return customProperties;
    }

    @SuppressWarnings("unchecked")
    private static <T> T getCustomClassInstance(String customClassName) {
        try {
            Class<?> clazz = Class.forName(customClassName);
            Constructor<?> ctor = clazz.getConstructor();
            return (T) ctor.newInstance();
        } catch (Exception e) {
            String msg = MessageFormat.format("Cannot create class instance for [{0}] due to [{1}]", customClassName, e.getMessage());
            throw new IllegalStateException(msg, e);
        }
    }

    private static PropertyVisibilityStrategy getPropertyVisibilityStrategyClass(Config config) {
        String className = config.getOptionalValue(PROPERTY_VISIBILITY_STRATEGY_CLASS, String.class).orElse(DEFAULT_FIELD_ONLY_VISIBILITY_STRATEGY);
        return getCustomClassInstance(className);
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
        return config.getOptionalValue(FAIL_ON_UNKNOWN_PROPERTIES, Boolean.class).orElse(true);
    }

    private static String getEncoding(Config config) {
        return config.getOptionalValue(ENCODING, String.class).orElse(StandardCharsets.UTF_8.name());
    }

    private static boolean getStrictIJson(Config config) {
        return config.getOptionalValue(STRICT_IJSON, Boolean.class).orElse(false);
    }

    private static String getPropertyNamingStrategy(Config config) {
        return config.getOptionalValue(PROPERTY_NAMING_STRATEGY, String.class).orElse(PropertyNamingStrategy.IDENTITY);
    }

    private static String getPropertyOrderStrategy(Config config) {
        return config.getOptionalValue(PROPERTY_ORDER_STRATEGY, String.class).orElse(PropertyOrderStrategy.LEXICOGRAPHICAL);
    }

    private static String getDateFormat(Config config) {
        return config.getOptionalValue(DATE_FORMAT, String.class).orElse(null);
    }

    private static Locale getLocale(Config config) {
        return config.getOptionalValue(LOCALE, String.class).map(Locale::forLanguageTag).orElse(Locale.getDefault());
    }

}
