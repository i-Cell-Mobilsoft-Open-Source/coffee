/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 i-Cell Mobilsoft Zrt.
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

import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.Optional;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbException;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.se.api.exception.JsonConversionException;
import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * Json util. Based on JSON-B only, the Gson based methods are in {@link GsonUtil}.
 *
 * @author imre.scheffer
 * @author bucherarnold
 * @since 1.0.0
 */
public class JsonUtil {

    private static final Logger LOGGER = Logger.getLogger(JsonUtil.class);
    private static final String ERROR_JSON_TO_OBJECT = "Error in converting JSON to object [{0}]";
    private static final String ERROR_OBJECT_TO_JSON = "Error in converting object [{0}] to JSON";
    private static final String CONVERTING_TO_OBJECT_SUCCESSFUL_0 = "Converting to Object successful: [{0}]";
    private static final String CONVERTING_TO_JSON_SUCCESSFUL_0 = "Converting to JSON successful: [{0}]";

    private static Jsonb jsonb;

    /**
     * Private constructor
     */
    private JsonUtil() {
    }

    /**
     * Get jsonb context with lazy initialization
     *
     * @return {@link Jsonb}
     */
    public static Jsonb getJsonbContext() {
        if (jsonb == null) {
            jsonb = JsonbUtil.getContext();
        }
        return jsonb;
    }

    /**
     * Converting DTO object to JSON string, throws exception with OPERATION_FAILED fault type if any error occurred
     *
     * @param dto
     *            DTO object
     * @return JSON String
     * @throws JsonConversionException
     *             exception during serialization process
     */
    public static String toJson(Object dto) throws JsonConversionException {
        try {
            String json = getJsonbContext().toJson(dto);
            LOGGER.debug(CONVERTING_TO_JSON_SUCCESSFUL_0, StringUtils.abbreviate(json, 1000));
            return json;
        } catch (JsonbException e) {
            throw new JsonConversionException(MessageFormat.format(ERROR_OBJECT_TO_JSON, dto.getClass()), e);
        }
    }

    /**
     * Generate dto to json and appends it to the passed writer
     *
     * @param dto
     *            object to convert to json
     * @param writer
     *            the output of the conversion
     */
    public static void toJson(Object dto, Writer writer) {
        getJsonbContext().toJson(dto, writer);
    }

    /**
     * Converting DTO object to JSON string without throwing exception. In case of any error occurs, an empty optional is returned
     *
     * @param dto
     *            DTO object
     * @return optional of JSON String
     */
    public static Optional<String> toJsonOpt(Object dto) {
        try {
            String json = getJsonbContext().toJson(dto);
            LOGGER.debug(CONVERTING_TO_JSON_SUCCESSFUL_0, StringUtils.abbreviate(json, 1000));
            return Optional.ofNullable(json);
        } catch (JsonbException e) {
            LOGGER.warn(MessageFormat.format(ERROR_OBJECT_TO_JSON, dto.getClass()), e);
            return Optional.empty();
        }
    }

    /**
     * Converting JSON string to DTO object of a given type, throws exception with OPERATION_FAILED fault type if any error occurred
     *
     * @param <T>
     *            type of returned object
     * @param json
     *            JSON String
     * @param typeOfT
     *            type of returned object
     * @return object
     * @throws JsonConversionException
     *             exception during deserialization process
     */
    public static <T> T toObject(String json, Type typeOfT) throws JsonConversionException {
        try {
            T dto = getJsonbContext().fromJson(json, typeOfT);
            LOGGER.debug(CONVERTING_TO_OBJECT_SUCCESSFUL_0, dto);
            return dto;
        } catch (Exception e) {
            throw new JsonConversionException(MessageFormat.format(ERROR_JSON_TO_OBJECT, typeOfT.getClass()), e);
        }
    }

    /**
     * Converting JSON string to DTO object of a given type, throws exception with OPERATION_FAILED fault type if any error occurred
     *
     * @param <T>
     *            type of returned object
     * @param json
     *            JSON String
     * @param clazz
     *            class of returned object
     * @return object
     * @throws JsonConversionException
     *             exception during deserialization process
     */
    public static <T> T toObject(String json, Class<T> clazz) throws JsonConversionException {
        try {
            T dto = getJsonbContext().fromJson(json, clazz);
            LOGGER.debug(CONVERTING_TO_OBJECT_SUCCESSFUL_0, dto);
            return dto;
        } catch (Exception e) {
            throw new JsonConversionException(MessageFormat.format(ERROR_JSON_TO_OBJECT, clazz.getClass()), e);
        }
    }

    /**
     * Converting JSON string to DTO object of a given type, throws exception with OPERATION_FAILED fault type if any error occurred
     *
     * @param <T>
     *            type of returned object
     * @param reader
     *            JSON reader
     * @param typeOfT
     *            type of returned object
     * @return object
     * @throws JsonConversionException
     *             exception during deserialization process
     */
    public static <T> T toObject(Reader reader, Type typeOfT) throws JsonConversionException {
        try {
            T dto = getJsonbContext().fromJson(reader, typeOfT);
            LOGGER.debug(CONVERTING_TO_OBJECT_SUCCESSFUL_0, dto);
            return dto;
        } catch (Exception e) {
            throw new JsonConversionException(MessageFormat.format(ERROR_JSON_TO_OBJECT, typeOfT.getClass()), e);
        }
    }

    /**
     * Converting JSON string to DTO object of a given type, throws exception with OPERATION_FAILED fault type if any error occurred
     *
     * @param <T>
     *            type of returned object
     * @param reader
     *            JSON reader
     * @param clazz
     *            class of returned object
     * @return object
     * @throws JsonConversionException
     *             exception during deserialization process
     */
    public static <T> T toObject(Reader reader, Class<T> clazz) throws JsonConversionException {
        try {
            T dto = getJsonbContext().fromJson(reader, clazz);
            LOGGER.debug(CONVERTING_TO_OBJECT_SUCCESSFUL_0, dto);
            return dto;
        } catch (Exception e) {
            throw new JsonConversionException(MessageFormat.format(ERROR_JSON_TO_OBJECT, clazz.getClass()), e);
        }
    }

    /**
     * Converting JSON string to DTO object of a given type without throwing exception. In case of any error occurs, an empty optional is returned
     *
     * @param <T>
     *            optional type of returned object
     * @param json
     *            JSON String
     * @param classType
     *            class of returned object
     * @return optional object
     */
    public static <T> Optional<T> toObjectOpt(String json, Class<T> classType) {
        try {
            T dto = getJsonbContext().fromJson(json, classType);
            LOGGER.debug(CONVERTING_TO_OBJECT_SUCCESSFUL_0, dto);
            return Optional.ofNullable(dto);
        } catch (JsonbException e) {
            LOGGER.warn(MessageFormat.format(ERROR_JSON_TO_OBJECT, classType), e);
            return Optional.empty();
        }
    }

}
