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
package hu.icellmobilsoft.coffee.rest.validation.json;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.ParseException;
import org.apache.http.entity.ContentType;

import hu.icellmobilsoft.coffee.cdi.logger.LogProducer;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.rest.validation.xml.JaxbTool;
import hu.icellmobilsoft.coffee.rest.validation.xml.XmlMessageBodyReaderBase;
import hu.icellmobilsoft.coffee.cdi.annotation.xml.ValidateXML;
import hu.icellmobilsoft.coffee.rest.validation.xml.exception.BaseProcessingExceptionWrapper;
import hu.icellmobilsoft.coffee.rest.validation.xml.exception.XsdProcessingException;
import hu.icellmobilsoft.coffee.rest.validation.xml.reader.IJsonRequestVersionReader;
import hu.icellmobilsoft.coffee.tool.gson.JsonUtil;

/**
 * JSON kiterjesztése az XML/XSD alapon működő megvalósításnak.<br>
 * Működési alapja hogy a JSON inputStream-ből DTO class keletkezik majd annak az értékei validációs XML marshaller-re van küldve.
 *
 * <pre>
 * &#64;Provider
 * &#64;Consumes({ MediaType.APPLICATION_JSON })
 * &#64;Priority(Priorities.ENTITY_CODER)
 * public class JsonRequestMessageBodyReader extends JsonMessageBodyReaderBase&lt;BasicRequestType&gt; {
 * }
 * </pre>
 *
 * <br>
 * 
 * @param <T>
 *            message body type
 *
 * @see XmlMessageBodyReaderBase
 * @author m.petrenyi
 * @author imre.scheffer
 * @since 1.0.0
 */
public abstract class JsonMessageBodyReaderBase<T> implements MessageBodyReader<T> {

    @Inject
    private JaxbTool jaxbTool;

    @Inject
    private IJsonRequestVersionReader jsonRequestVersionReader;

    /**
     * Default constructor, constructs a new object.
     */
    public JsonMessageBodyReaderBase() {
        super();
    }

    /**
     * {@inheritDoc}
     *
     * Xsd validálásra kerülhet-e?
     */
    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return ArrayUtils.isNotEmpty(XmlMessageBodyReaderBase.getValidateIfPresent(annotations));
    }

    /**
     * {@inheritDoc}
     *
     * Ha van ValidateXML annotáció, akkor azt használja
     */
    @Override
    public T readFrom(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
            InputStream entityStream) {
        try {
            ValidateXML[] validates = XmlMessageBodyReaderBase.getValidateIfPresent(annotations);
            T obj = deserializeJson(type, getCharsetOrUTF8(httpHeaders), entityStream);
            String requestVersion = readRequestVersion(obj);
            String schemaPath = jaxbTool.getXsdPath(validates, requestVersion);
            jaxbTool.marshalXML(obj, schemaPath);
            return obj;
        } catch (BaseException e) {
            throw new BaseProcessingExceptionWrapper(e);
        }
    }

    /**
     * Get the charset from the HTTP {@value HttpHeaders#CONTENT_TYPE} header or return UTF-8 in case of any issue
     * 
     * @param httpHeaders
     *            the read-only HTTP headers associated with HTTP entity.
     * @return the charset or UTF-8 if its unknown
     */
    private Charset getCharsetOrUTF8(MultivaluedMap<String, String> httpHeaders) {
        try {
            ContentType contentType = ContentType.parse(httpHeaders.getFirst(HttpHeaders.CONTENT_TYPE));
            if (contentType.getCharset() != null) {
                return contentType.getCharset();
            }
            LogProducer.logToAppLogger(log -> log.trace("Content-Type charset is not set - returning UTF-8 by default"), getClass());
        } catch (ParseException | IllegalArgumentException e) {
            LogProducer.logToAppLogger(log -> log.warn("Unknown charset in Content-Type! Returning UTF-8 by default", e), getClass());
        }
        return StandardCharsets.UTF_8;
    }

    /**
     * Reads request version from request entity.
     * 
     * @param object
     *            object to read from
     * @return request version as {@link String}
     * @throws XsdProcessingException
     *             if version cannot be read from the object
     */
    protected String readRequestVersion(Object object) throws XsdProcessingException {
        try {
            return jsonRequestVersionReader.readFromJSON(object);
        } catch (TechnicalException e) {
            throw new XsdProcessingException(CoffeeFaultType.INVALID_INPUT,
                    MessageFormat.format("Error in reading object [class: {0}]: [{1}]", object.getClass(), e.getLocalizedMessage()), e);
        }
    }

    /**
     * Creates object from json inputStream.
     * 
     * @param type
     *            type to deserialize into
     * @param charSet
     *            the character set of the json
     * @param entityStream
     *            input stream of entity
     * @return deserialized object
     * @throws XsdProcessingException
     *             if the json can not be deserialized
     */
    protected T deserializeJson(Class<T> type, Charset charSet, InputStream entityStream) throws XsdProcessingException {
        try {
            return JsonUtil.toObjectGson(new InputStreamReader(entityStream, charSet), type);
        } catch (Exception e) {
            throw new XsdProcessingException(CoffeeFaultType.INVALID_INPUT, e.getMessage(), e);
        }
    }

}
