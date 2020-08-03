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
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.Date;

import javax.enterprise.inject.Vetoed;
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

    private static final Logger LOGGER = hu.icellmobilsoft.coffee.cdi.logger.LogProducer.getStaticDefaultLogger(JsonUtil.class);

    /**
     * Converting DTO object to JSON string without throwing exception
     *
     * @param dto
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
     * @param json
     *            JSON String
     * @param classType
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
     * Converting DTO object to JSON string
     *
     * @param dto
     * @return JSON String
     * @throws BaseException
     */
    public static String toJsonEx(Object dto) throws BaseException {
        try {
            String json = toJsonGson(dto);
            LOGGER.debug("Converting to JSON successful: [" + StringUtils.abbreviate(json, 1000) + "]");
            return json;
        } catch (Exception e) {
            throw new BaseException("Error in converting dto [" + dto.getClass() + "] to String: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * Converting JSON string to DTO object without throwing exception
     *
     * @param json
     *            JSON String
     * @param classType
     * @return object
     * @throws BaseException
     */
    public static <T> T toObjectEx(String json, Class<T> classType) throws BaseException {
        try {
            T dto = toObjectGson(json, classType);
            LOGGER.debug("Converting to Object successful: [" + dto + "]");
            return dto;
        } catch (Exception e) {
            throw new BaseException("Error in converting json [" + json + "] to [" + classType + "]: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * <p>toJsonGson.</p>
     */
    public static String toJsonGson(Object dto) {
        Gson gson = initGson();

        return gson.toJson(dto);
    }

    /**
     * Convert JSON String to DTO object without throwing exception
     *
     * @param json
     *            in String
     * @param classType
     *            json DTO class
     * @return DTO
     */
    public static <T> T toObjectGson(String json, Class<T> classType) {
        Gson gson = initGson();

        try {
            return gson.fromJson(json, classType);
        } catch (JsonSyntaxException e) {
            LOGGER.warn("Error in parse JSON [" + e.getLocalizedMessage() + "], try lenient... ");
            JsonReader reader = new JsonReader(new StringReader(json));
            reader.setLenient(true);
            return gson.fromJson(reader, classType);
        }
    }

    /**
     * Convert JSON String to DTO object without throwing exception
     *
     * @param json
     *            in String
     * @param classType
     *            json DTO class
     * @return DTO
     */
    public static <T> T toObjectGson(Reader reader, Class<T> classType) {
        Gson gson = initGson();

        try {
            return gson.fromJson(reader, classType);
        } catch (JsonSyntaxException e) {
            LOGGER.warn("Error in parse JSON [" + e.getLocalizedMessage() + "], try lenient... ");
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
                .registerTypeHierarchyAdapter(byte[].class, new ByteArrayConverter()) //
                .create();
    }
}
