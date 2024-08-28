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

import hu.icellmobilsoft.coffee.rest.provider.FieldOnlyVisibilityStrategy;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.coffee.tool.utils.config.ConfigUtil;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.adapter.JsonbAdapter;
import jakarta.json.bind.config.BinaryDataStrategy;
import jakarta.json.bind.config.PropertyNamingStrategy;
import jakarta.json.bind.config.PropertyVisibilityStrategy;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.bind.serializer.JsonbSerializer;
import org.eclipse.microprofile.config.Config;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * {@link Jsonb} object creator util class.
 *
 * <pre>
 * coffee:
 *   jsonb:
 *     config:
 *       {@value JsonbUtil#PROPERTY_VISIBILITY_STRATEGY_CLASS_POSTFIX}: "hu.icellmobilsoft.coffee.rest.provider.FieldOnlyVisibilityStrategy"
 *       {@value JsonbUtil#BINARY_DATA_STRATEGY_POSTFIX}: "BASE_64"
 * </pre>
 *
 * @author speter555
 * @since 2.6.0
 */
public class JsonbUtil {

    /**
     * Config delimiter
     */
    private static final String KEY_DELIMITER = ".";

    private static final String PROPERTY_VISIBILITY_STRATEGY_CLASS_POSTFIX = "propertyVisibilityStrategyClass";
    private static final String BINARY_DATA_STRATEGY_POSTFIX = "binaryDataStrategy";
    private static final String FORMATTING_POSTFIX = "formatting";
    private static final String ENCODING_POSTFIX = "encoding";
    private static final String NULL_VALUES_POSTFIX = "nullValues";
    private static final String STRICT_IJSON_POSTFIX = "strictIJSON";
    private static final String PROPERTY_NAMING_STRATEGY_POSTFIX = "propertyNamingStrategy";
    private static final String PROPERTY_ORDER_STRATEGY_POSTFIX = "propertyOrderStrategy";
    private static final String DATE_FORMAT_POSTFIX = "dateFormat";
    private static final String LOCALE_POSTFIX = "locale";
    private static final String ADAPTERS_POSTFIX = "adapters";
    private static final String SERIALIZERS_POSTFIX = "serializers";
    private static final String DESERIALIZERS_POSTFIX = "deserializers";
    private static final String CREATOR_PARAMETERS_REQUIRED_POSTFIX = "creatorParametersRequired";

    /**
     * Prefix for all configs
     */
    public static final String JSONB_CONFIG_PREFIX = "coffee.jsonb.config";

    private static final Jsonb JSONB;

//    @Inject
//    private static Instance<JsonbAdapter> jsonbAdapters;

    static {
        Config config = ConfigUtil.getInstance().defaultConfig();
        JsonbConfig jsonbConfig = new JsonbConfig()
                // property visibility strategy setting
                .withPropertyVisibilityStrategy(getPropertyVisibilityStrategyClass(config))
                .withBinaryDataStrategy(getBinaryDataStrategy(config));

        getStringConfig(config, BINARY_DATA_STRATEGY_POSTFIX)
                .ifPresent(jsonbConfig::withBinaryDataStrategy);

        getBooleanConfig(config, FORMATTING_POSTFIX)
                .ifPresent(jsonbConfig::withFormatting);

        getBooleanConfig(config, NULL_VALUES_POSTFIX)
                .ifPresent(jsonbConfig::withNullValues);

        getStringConfig(config, ENCODING_POSTFIX)
                .ifPresent(jsonbConfig::withEncoding);

        getBooleanConfig(config, STRICT_IJSON_POSTFIX)
                .ifPresent(jsonbConfig::withStrictIJSON);

        getPropertyNamingStrategyClass(config)
                .ifPresent(jsonbConfig::withPropertyNamingStrategy);

        getStringConfig(config, PROPERTY_ORDER_STRATEGY_POSTFIX)
                .ifPresent(jsonbConfig::withPropertyOrderStrategy);

        getStringConfig(config, DATE_FORMAT_POSTFIX)
                .ifPresent(dateFormat -> jsonbConfig.withDateFormat(dateFormat, getLocaleConfig(config).orElse(Locale.getDefault())));

        getLocaleConfig(config)
                .ifPresent(jsonbConfig::withLocale);

        // Apply adapters, serializers, and deserializers only if they are configured
        JsonbAdapter[] adapters = getAdapters(config);
        if (adapters.length > 0) {
            jsonbConfig.withAdapters(adapters);
        }

        JsonbSerializer[] serializers = getSerializers(config);
        if (serializers.length > 0) {
            jsonbConfig.withSerializers(serializers);
        }

        JsonbDeserializer[] deserializers = getDeserializers(config);
        if (deserializers.length > 0) {
            jsonbConfig.withDeserializers(deserializers);
        }

        JSONB = JsonbBuilder.newBuilder().withConfig(jsonbConfig).build();
    }

    /**
     * {@link Jsonb} instance getter.
     *
     * @return configured {@link Jsonb} instance
     */
    public static Jsonb getContext() {
        return JSONB;
    }

    private static PropertyVisibilityStrategy getPropertyVisibilityStrategyClass(Config config) {
        String className = config.getOptionalValue(joinKey(PROPERTY_VISIBILITY_STRATEGY_CLASS_POSTFIX), String.class)
                .orElse("hu.icellmobilsoft.coffee.rest.provider.FieldOnlyVisibilityStrategy");
        try {
            return (PropertyVisibilityStrategy) Class.forName(className).getConstructor().newInstance();
        } catch (Exception e) {
            Logger.getLogger(JsonbUtil.class)
                    .warn(
                            MessageFormat.format(
                                    "The PropertyVisibilityStrategy class in the [{0}] config with value [{1}] has a problem ",
                                    joinKey(PROPERTY_VISIBILITY_STRATEGY_CLASS_POSTFIX),
                                    className),
                            e);
            return new FieldOnlyVisibilityStrategy();
        }
    }

    private static String getBinaryDataStrategy(Config config) {
        return config.getOptionalValue(joinKey(BINARY_DATA_STRATEGY_POSTFIX), String.class).orElse(BinaryDataStrategy.BASE_64);
    }

    private static Optional<String> getStringConfig(Config config, String keyPostfix) {
        return config.getOptionalValue(joinKey(keyPostfix), String.class);
    }

    private static Optional<Boolean> getBooleanConfig(Config config, String keyPostfix) {
        return config.getOptionalValue(joinKey(keyPostfix), Boolean.class);
    }

    private static Optional<Locale> getLocaleConfig(Config config) {
        return config.getOptionalValue(joinKey(LOCALE_POSTFIX), String.class).map(Locale::forLanguageTag);
    }

    private static Optional<PropertyNamingStrategy> getPropertyNamingStrategyClass(Config config) {
        String className = config.getOptionalValue(joinKey(PROPERTY_NAMING_STRATEGY_POSTFIX), String.class)
                .orElse(null);
        try {
            return Optional.of((PropertyNamingStrategy) Class.forName(className).getConstructor().newInstance());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private static JsonbAdapter[] getAdapters(Config config) {
        List<JsonbAdapter> jsonbAdapters = new ArrayList<>();
        Optional<List<String>> optionalAdapterClassNames = config.getOptionalValues(joinKey(ADAPTERS_POSTFIX), String.class);

        if (optionalAdapterClassNames.isPresent()) {
            List<String> adapterClassNames = optionalAdapterClassNames.get();
            for (String adapterClassName : adapterClassNames) {
                try {
                    jsonbAdapters.add((JsonbAdapter) Class.forName(adapterClassName).getConstructor().newInstance());
                } catch (Exception e) {
                    Logger.getLogger(JsonbUtil.class)
                            .warn(
                                    MessageFormat.format(
                                            "The JsonAdapter class in the [{0}] config with value [{1}] has a problem ",
                                            joinKey(ADAPTERS_POSTFIX),
                                            adapterClassName),
                                    e);
                }
            }
        }
        return jsonbAdapters.toArray(new JsonbAdapter[0]);
    }

    private static JsonbSerializer[] getSerializers(Config config) {
        List<JsonbSerializer> jsonbSerializers = new ArrayList<>();
        Optional<List<String>> optionalSerializerClassNames = config.getOptionalValues(joinKey(SERIALIZERS_POSTFIX), String.class);

        if (optionalSerializerClassNames.isPresent()) {
            List<String> serializerClassNames = optionalSerializerClassNames.get();
            for (String serializerClassName : serializerClassNames) {
                try {
                    jsonbSerializers.add((JsonbSerializer) Class.forName(serializerClassName).getConstructor().newInstance());
                } catch (Exception e) {
                    Logger.getLogger(JsonbUtil.class)
                            .warn(
                                    MessageFormat.format(
                                            "The JsonbSerializer class in the [{0}] config with value [{1}] has a problem ",
                                            joinKey(SERIALIZERS_POSTFIX),
                                            serializerClassName),
                                    e);
                }
            }
        }

        return jsonbSerializers.toArray(new JsonbSerializer[0]);
    }

    private static JsonbDeserializer[] getDeserializers(Config config) {
        List<JsonbDeserializer> jsonbDeserializers = new ArrayList<>();
        Optional<List<String>> optionalDeserializerClassNames = config.getOptionalValues(joinKey(DESERIALIZERS_POSTFIX), String.class);

        if (optionalDeserializerClassNames.isPresent()) {
            List<String> deserializerClassNames = optionalDeserializerClassNames.get();
            for (String deserializerClassName : deserializerClassNames) {
                try {
                    jsonbDeserializers.add((JsonbDeserializer) Class.forName(deserializerClassName).getConstructor().newInstance());
                } catch (Exception e) {
                    Logger.getLogger(JsonbUtil.class)
                            .warn(
                                    MessageFormat.format(
                                            "The JsonbDeserializer class in the [{0}] config with value [{1}] has a problem ",
                                            joinKey(DESERIALIZERS_POSTFIX),
                                            deserializerClassName),
                                    e);
                }
            }
        }

        return jsonbDeserializers.toArray(new JsonbDeserializer[0]);
    }

    private static String joinKey(String key) {
        return String.join(KEY_DELIMITER, JSONB_CONFIG_PREFIX, key);
    }

    /**
     * Private constructor
     */
    private JsonbUtil() {
        super();
    }
}
