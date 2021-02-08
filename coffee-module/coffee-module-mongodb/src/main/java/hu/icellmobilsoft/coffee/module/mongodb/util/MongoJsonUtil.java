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
package hu.icellmobilsoft.coffee.module.mongodb.util;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.enterprise.inject.Vetoed;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.bson.conversions.Bson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.mongodb.BasicDBObject;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.coffee.tool.gson.ClassTypeAdapter;
import hu.icellmobilsoft.coffee.tool.utils.date.DateUtil;
import hu.icellmobilsoft.coffee.tool.utils.date.DateXmlUtil;

/**
 * Json util.
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Vetoed
public class MongoJsonUtil {

    private static Logger LOGGER = hu.icellmobilsoft.coffee.cdi.logger.LogProducer.getStaticDefaultLogger(MongoJsonUtil.class);

    private static final String DATE_PROPERTY = "$date";

    /**
     * Converts DTO object to JSON string without throwing exception.
     *
     * @param dto
     *            DTO to convert
     * @return JSON String
     * @see #toJsonEx(Object)
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
     * Converts JSON string to DTO object without throwing exception
     *
     * @param <T>
     *            JSON DTO type
     * @param json
     *            JSON String
     * @param classType
     *            DTO class
     * @return DTO object or null if any exception occurs during conversion
     * @see #toObjectEx(String, Class)
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
     * Converts DTO object to JSON string.
     *
     * @param dto
     *            DTO
     * @return JSON String
     * @throws BaseException
     *             if any exception occurs during conversion
     * @see #toJsonGson(Object)
     */
    public static String toJsonEx(Object dto) throws BaseException {
        try {
            String json = toJsonGson(dto);
            // String json = toJsonEclipselink(dto);
            LOGGER.debug("Converting to JSON successful: [" + json + "]");
            return json;
        } catch (Exception e) {
            throw new BaseException("Error in converting dto [" + dto.getClass() + "] to String: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * Converts JSON string to DTO object.
     *
     * @param <T>
     *            JSON DTO type
     * @param json
     *            JSON String
     * @param classType
     *            DTO class
     * @return DTO object
     * @throws BaseException
     *             if any exception occurs during conversion
     * @see #toObjectGson(String, Class)
     */
    public static <T> T toObjectEx(String json, Class<T> classType) throws BaseException {
        try {
            T dto = toObjectGson(json, classType);
            // T dto = toObjectEclipselink(json, classType);
            LOGGER.debug("Converting to Object successful: [" + dto + "]");
            return dto;
        } catch (Exception e) {
            throw new BaseException("Error in converting json [" + json + "] to [" + classType + "]: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * Converts DTO object to JSON string.
     *
     * @param dto
     *            DTO
     * @return JSON String
     */
    public static String toJsonGson(Object dto) {
        // ez mehetne a class konstruktorba is
        Gson gson = new GsonBuilder().disableHtmlEscaping().registerTypeAdapter(Class.class, new ClassTypeAdapter())
                .registerTypeAdapter(XMLGregorianCalendar.class, new XMLGregorianCalendarConverter()).create();

        return gson.toJson(dto);
    }

    /**
     * Converts JSON String to DTO object without throwing exception.
     *
     * @param <T>
     *            JSON DTO type
     * @param json
     *            in String
     * @param classType
     *            JSON DTO class
     * @return DTO
     */
    public static <T> T toObjectGson(String json, Class<T> classType) {
        Gson gson = new GsonBuilder().disableHtmlEscaping().registerTypeAdapter(Class.class, new ClassTypeAdapter())
                .registerTypeAdapter(XMLGregorianCalendar.class, new XMLGregorianCalendarConverter()).create();

        try {
            return gson.fromJson(json, classType);
        } catch (JsonSyntaxException e) {
            LOGGER.warn("Error in parse JSON [{0}], try lenient... ", e.getLocalizedMessage(), e);
            JsonReader reader = new JsonReader(new StringReader(json));
            reader.setLenient(true);
            return gson.fromJson(reader, classType);
        }
    }

    /** {@inheritDoc} */
    public static class XMLGregorianCalendarConverter implements JsonSerializer<XMLGregorianCalendar>, JsonDeserializer<XMLGregorianCalendar> {

        /** {@inheritDoc} */
        public JsonElement serialize(XMLGregorianCalendar calendar, Type type, JsonSerializationContext jsonSerializationContext) {
            DateTimeFormatter f = DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.systemDefault());
            String isoString = f.format(calendar.toGregorianCalendar().toInstant());
            JsonObject jo = new JsonObject();
            jo.addProperty(DATE_PROPERTY, isoString);
            return jo;
            // return JsonPrimitive(calendar.toXMLFormat());
        }

        /** {@inheritDoc} */
        public XMLGregorianCalendar deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
            try {
                try {

                    String value;
                    if (jsonElement.isJsonArray()) {
                        value = jsonElement.getAsJsonArray().get(0).getAsJsonObject().get(DATE_PROPERTY).getAsString();
                    } else if (jsonElement.isJsonObject()) {
                        value = jsonElement.getAsJsonObject().get(DATE_PROPERTY).getAsString();
                    } else if (jsonElement.isJsonNull()) {
                        return null;
                    } else {
                        value = jsonElement.getAsString();
                    }
                    if (NumberUtils.isCreatable(value)) {
                        GregorianCalendar calendar = new GregorianCalendar();
                        calendar.setTimeInMillis(Long.parseLong(value));
                        return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
                    }
                    return DateXmlUtil.toXMLGregorianCalendarFromISO(value);
                } catch (NumberFormatException e) {
                    return DatatypeFactory.newInstance().newXMLGregorianCalendar(jsonElement.getAsString());
                }
            } catch (Exception e) {
                return null;
            }
        }
    }

    /**
     * Converts JSON {@link String} to {@link BasicDBObject} and adds it to given BSON list.
     *
     * @param bsonList
     *            BSON list to add to
     * @param json
     *            JSON to convert and add to BSON list
     * @throws BaseException
     *             if params are null or empty
     */
    public static void addJsonToBsonList(List<Bson> bsonList, String json) throws BaseException {
        if (bsonList == null || StringUtils.isBlank(json)) {
            throw new BaseException("bsonList is null or json is blank!");
        }
        BasicDBObject row = MongoUtil.jsonToBasicDbObject(json);
        bsonList.add(row);
    }

    /**
     * Converts JSON {@link String} array to {@link BasicDBObject}s and adds them to given BSON list.
     *
     * @param bsonList
     *            BSON list to add to
     * @param jsons
     *            JSON list to convert and add to BSON list
     * @throws BaseException
     *             if params are null or empty
     * @see #addJsonToBsonList(List, String)
     */
    public static void addJsonsToBsonList(List<Bson> bsonList, String... jsons) throws BaseException {
        if (bsonList == null || jsons == null || jsons.length == 0) {
            throw new BaseException("bsonList is null or jsons is null or empty!");
        }
        for (String json : jsons) {
            addJsonToBsonList(bsonList, json);
        }
    }

    /**
     * Converts {@link Date} to Mongo {@link String}.
     *
     * @param date
     *            {@code Date} to convert
     * @return converted {@code String}
     * @throws BaseException
     *             if {@code date} is null
     */
    public static String dateToString(Date date) throws BaseException {
        if (date == null) {
            throw new BaseException("date is null!");
        }

        DateTimeFormatter f = DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.systemDefault());
        return f.format(DateUtil.toCalendar(date).toInstant());
    }
}
