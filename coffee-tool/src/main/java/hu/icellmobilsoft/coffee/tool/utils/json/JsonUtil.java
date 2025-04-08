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
import java.io.StringReader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.YearMonth;
import java.util.Date;
import java.util.Optional;

import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbException;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.se.api.exception.JsonConversionException;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.coffee.tool.gson.ByteArrayConverter;
import hu.icellmobilsoft.coffee.tool.gson.ClassTypeAdapter;
import hu.icellmobilsoft.coffee.tool.gson.DateConverter;
import hu.icellmobilsoft.coffee.tool.gson.DurationConverter;
import hu.icellmobilsoft.coffee.tool.gson.LocalDateConverter;
import hu.icellmobilsoft.coffee.tool.gson.OffsetDateTimeConverter;
import hu.icellmobilsoft.coffee.tool.gson.OffsetTimeConverter;
import hu.icellmobilsoft.coffee.tool.gson.XMLGregorianCalendarConverter;
import hu.icellmobilsoft.coffee.tool.gson.YearMonthConverter;

/**
 * Json util.
 *
 * @author imre.scheffer
 * @author bucherarnold
 * @since 1.0.0
 */
public class JsonUtil {

    private static final Logger LOGGER = Logger.getLogger(JsonUtil.class);
    private static final String ERROR_IN_PARSE_JSON_0_TRY_LENIENT = "Error in parse JSON [{0}], try lenient... ";
    private static final String ERROR_JSON_TO_OBJECT = "Error in converting JSON to object [{0}]";
    private static final String ERROR_OBJECT_TO_JSON = "Error in converting object [{0}] to JSON";
    private static final String CONVERTING_TO_OBJECT_SUCCESSFUL_0 = "Converting to Object successful: [{0}]";
    private static final String CONVERTING_TO_JSON_SUCCESSFUL_0 = "Converting to JSON successful: [{0}]";

    private static Gson gson;
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
     * Get gson context with lazy initialization
     *
     * @return {@link Gson}
     */
    public static Gson getGsonConfig() {
        if (gson == null) {
            gson = initGson();
        }
        return gson;
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

    /**
     * Converting JSON string to DTO object
     *
     * @param <T>
     *            type of returned object
     * @param json
     *            JSON String
     * @param typeOfT
     *            type of returned object
     * @return object
     * @throws BaseException
     *             exception
     */
    @Deprecated(since = "2.9.0", forRemoval = true)
    public static <T> T toObjectUncheckedEx(String json, Type typeOfT) throws BaseException {
        try {
            T dto = toObjectUncheckedGson(json, typeOfT);
            LOGGER.debug(CONVERTING_TO_OBJECT_SUCCESSFUL_0, dto);
            return dto;
        } catch (Exception e) {
            throw new BaseException(
                    CoffeeFaultType.OPERATION_FAILED,
                    "Error in converting json [" + json + "] to [" + typeOfT + "]: " + e.getLocalizedMessage(),
                    e);
        }
    }

    /**
     * Convert JSON String to DTO object without throwing exception
     *
     * @param <T>
     *            type of returned object
     * @param json
     *            JSON String
     * @param typeOfT
     *            type of returned object
     * @return DTO
     */
    @Deprecated(since = "2.9.0", forRemoval = true)
    public static <T> T toObjectUncheckedGson(String json, Type typeOfT) {
        try {
            return getGsonConfig().fromJson(json, typeOfT);
        } catch (JsonSyntaxException e) {
            LOGGER.warn(ERROR_IN_PARSE_JSON_0_TRY_LENIENT, e.getLocalizedMessage());
            JsonReader reader = new JsonReader(new StringReader(json));
            reader.setLenient(true);
            return getGsonConfig().fromJson(reader, typeOfT);
        }
    }

    /**
     * Convert JSON String to DTO object without throwing exception
     *
     * @param <T>
     *            type of returned object
     * @param reader
     *            JSON reader
     * @param typeOfT
     *            type of returned object
     * @return DTO
     */
    @Deprecated(since = "2.9.0", forRemoval = true)
    public static <T> T toObjectUncheckedGson(Reader reader, Type typeOfT) {
        try {
            return getGsonConfig().fromJson(reader, typeOfT);
        } catch (JsonSyntaxException e) {
            LOGGER.warn(ERROR_IN_PARSE_JSON_0_TRY_LENIENT, e.getLocalizedMessage());
            JsonReader jsonreader = new JsonReader(reader);
            jsonreader.setLenient(true);
            return getGsonConfig().fromJson(jsonreader, typeOfT);
        }
    }

    /**
     * Converting JSON string to DTO object without throwing exception
     *
     * @param <T>
     *            type of returned object
     * @param json
     *            JSON String
     * @param typeOfT
     *            type of returned object
     * @return object
     */
    @Deprecated(since = "2.9.0", forRemoval = true)
    public static <T> T toObjectUnchecked(String json, Type typeOfT) {
        try {
            return toObjectUncheckedEx(json, typeOfT);
        } catch (BaseException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            return null;
        }
    }

    /**
     * Converting DTO object to JSON string
     *
     * @param dto
     *            DTO object
     * @return JSON String
     * @throws BaseException
     *             exception
     */
    @Deprecated(since = "2.9.0", forRemoval = true)
    public static String toJsonEx(Object dto) throws BaseException {
        try {
            String json = toJsonGson(dto);
            LOGGER.debug(CONVERTING_TO_JSON_SUCCESSFUL_0, StringUtils.abbreviate(json, 1000));
            return json;
        } catch (Exception e) {
            throw new BaseException(
                    CoffeeFaultType.OPERATION_FAILED,
                    "Error in converting dto [" + dto.getClass() + "] to String: " + e.getLocalizedMessage(),
                    e);
        }
    }

    /**
     * Converting JSON string to DTO object
     *
     * @param <T>
     *            type of returned object
     * @param json
     *            JSON String
     * @param classType
     *            class of returned object
     * @return object
     * @throws BaseException
     *             exception
     */
    @Deprecated(since = "2.9.0", forRemoval = true)
    public static <T> T toObjectEx(String json, Class<T> classType) throws BaseException {
        try {
            T dto = toObjectGson(json, classType);
            LOGGER.debug(CONVERTING_TO_OBJECT_SUCCESSFUL_0, dto);
            return dto;
        } catch (Exception e) {
            throw new BaseException(
                    CoffeeFaultType.OPERATION_FAILED,
                    "Error in converting json [" + json + "] to [" + classType + "]: " + e.getLocalizedMessage(),
                    e);
        }
    }

    /**
     * Converting DTO object to JSON string
     *
     * @param dto
     *            DTO object
     * @return JSON String
     */
    @Deprecated(since = "2.9.0", forRemoval = true)
    public static String toJsonGson(Object dto) {
        return getGsonConfig().toJson(dto);
    }

    /**
     * Convert JSON String to DTO object without throwing exception
     *
     * @param <T>
     *            type of returned object
     * @param json
     *            JSON String
     * @param classType
     *            class of returned object
     * @return DTO
     */
    @Deprecated(since = "2.9.0", forRemoval = true)
    public static <T> T toObjectGson(String json, Class<T> classType) {
        try {
            return getGsonConfig().fromJson(json, classType);
        } catch (JsonSyntaxException e) {
            LOGGER.warn(ERROR_IN_PARSE_JSON_0_TRY_LENIENT, e.getLocalizedMessage());
            JsonReader reader = new JsonReader(new StringReader(json));
            reader.setLenient(true);
            return getGsonConfig().fromJson(reader, classType);
        }
    }

    /**
     * Convert JSON String to DTO object without throwing exception
     *
     * @param <T>
     *            type of returned object
     * @param reader
     *            JSON reader
     * @param classType
     *            class of returned object
     * @return DTO
     */
    @Deprecated(since = "2.9.0", forRemoval = true)
    public static <T> T toObjectGson(Reader reader, Class<T> classType) {
        try {
            return getGsonConfig().fromJson(reader, classType);
        } catch (JsonSyntaxException e) {
            LOGGER.warn(ERROR_IN_PARSE_JSON_0_TRY_LENIENT, e.getLocalizedMessage());
            JsonReader jsonreader = new JsonReader(reader);
            jsonreader.setLenient(true);
            return getGsonConfig().fromJson(jsonreader, classType);
        }
    }

    private static Gson initGson() {
        return new GsonBuilder().disableHtmlEscaping() //
                .registerTypeAdapter(Class.class, new ClassTypeAdapter()) //
                .registerTypeAdapter(XMLGregorianCalendar.class, new XMLGregorianCalendarConverter()) //
                .registerTypeAdapter(Date.class, new DateConverter()) //
                .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeConverter()) //
                .registerTypeAdapter(OffsetTime.class, new OffsetTimeConverter()) //
                .registerTypeAdapter(LocalDate.class, new LocalDateConverter()) //
                .registerTypeAdapter(Duration.class, new DurationConverter()) //
                .registerTypeAdapter(YearMonth.class, new YearMonthConverter()) //
                .registerTypeHierarchyAdapter(byte[].class, new ByteArrayConverter()) //
                .create();
    }
}
