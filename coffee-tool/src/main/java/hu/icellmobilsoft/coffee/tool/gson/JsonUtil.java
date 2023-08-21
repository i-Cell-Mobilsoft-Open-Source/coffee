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
package hu.icellmobilsoft.coffee.tool.gson;

import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.YearMonth;
import java.util.Date;

import javax.enterprise.inject.Vetoed;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * Json util.
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Vetoed
public class JsonUtil {

    private static final Logger LOGGER = Logger.getLogger(JsonUtil.class);
    private static final String ERROR_IN_PARSE_JSON_0_TRY_LENIENT = "Error in parse JSON [{0}], try lenient... ";
    private static final String CONVERTING_TO_OBJECT_SUCCESSFUL_0 = "Converting to Object successful: [{0}]";
    private static final String CONVERTING_TO_JSON_SUCCESSFUL_0 = "Converting to JSON successful: [{0}]";

    private JsonUtil() {
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
    public static <T> T toObjectUncheckedEx(String json, Type typeOfT) throws BaseException {
        try {
            T dto = toObjectUncheckedGson(json, typeOfT);
            LOGGER.debug(CONVERTING_TO_OBJECT_SUCCESSFUL_0, dto);
            return dto;
        } catch (Exception e) {
            throw new BaseException("Error in converting json [" + json + "] to [" + typeOfT + "]: " + e.getLocalizedMessage(), e);
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
    public static <T> T toObjectUncheckedGson(String json, Type typeOfT) {
        Gson gson = initGson();

        try {
            return gson.fromJson(json, typeOfT);
        } catch (JsonSyntaxException e) {
            LOGGER.warn(ERROR_IN_PARSE_JSON_0_TRY_LENIENT, e.getLocalizedMessage());
            JsonReader reader = new JsonReader(new StringReader(json));
            reader.setLenient(true);
            return gson.fromJson(reader, typeOfT);
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
    public static <T> T toObjectUncheckedGson(Reader reader, Type typeOfT) {
        Gson gson = initGson();

        try {
            return gson.fromJson(reader, typeOfT);
        } catch (JsonSyntaxException e) {
            LOGGER.warn(ERROR_IN_PARSE_JSON_0_TRY_LENIENT, e.getLocalizedMessage());
            JsonReader jsonreader = new JsonReader(reader);
            jsonreader.setLenient(true);
            return gson.fromJson(jsonreader, typeOfT);
        }
    }

    /**
     * Converting DTO object to JSON string without throwing exception
     *
     * @param dto
     *            DTO object
     * @return JSON String
     */
    public static String toJson(Object dto) {
        try {
            return toJsonEx(dto);
        } catch (BaseException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            return null;
        }
    }

    /**
     * Converting JSON string to DTO object without throwing exception
     *
     * @param <T>
     *            type of returned object
     * @param json
     *            JSON String
     * @param classType
     *            class of returned object
     * @return object
     */
    public static <T> T toObject(String json, Class<T> classType) {
        try {
            return toObjectEx(json, classType);
        } catch (BaseException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            return null;
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
    public static String toJsonEx(Object dto) throws BaseException {
        try {
            String json = toJsonGson(dto);
            LOGGER.debug(CONVERTING_TO_JSON_SUCCESSFUL_0, StringUtils.abbreviate(json, 1000));
            return json;
        } catch (Exception e) {
            throw new BaseException("Error in converting dto [" + dto.getClass() + "] to String: " + e.getLocalizedMessage(), e);
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
    public static <T> T toObjectEx(String json, Class<T> classType) throws BaseException {
        try {
            T dto = toObjectGson(json, classType);
            LOGGER.debug(CONVERTING_TO_OBJECT_SUCCESSFUL_0, dto);
            return dto;
        } catch (Exception e) {
            throw new BaseException("Error in converting json [" + json + "] to [" + classType + "]: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * Converting DTO object to JSON string
     *
     * @param dto
     *            DTO object
     * @return JSON String
     */
    public static String toJsonGson(Object dto) {
        Gson gson = initGson();

        return gson.toJson(dto);
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
    public static <T> T toObjectGson(String json, Class<T> classType) {
        Gson gson = initGson();

        try {
            return gson.fromJson(json, classType);
        } catch (JsonSyntaxException e) {
            LOGGER.warn(ERROR_IN_PARSE_JSON_0_TRY_LENIENT, e.getLocalizedMessage());
            JsonReader reader = new JsonReader(new StringReader(json));
            reader.setLenient(true);
            return gson.fromJson(reader, classType);
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
    public static <T> T toObjectGson(Reader reader, Class<T> classType) {
        Gson gson = initGson();

        try {
            return gson.fromJson(reader, classType);
        } catch (JsonSyntaxException e) {
            LOGGER.warn(ERROR_IN_PARSE_JSON_0_TRY_LENIENT, e.getLocalizedMessage());
            JsonReader jsonreader = new JsonReader(reader);
            jsonreader.setLenient(true);
            return gson.fromJson(jsonreader, classType);
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