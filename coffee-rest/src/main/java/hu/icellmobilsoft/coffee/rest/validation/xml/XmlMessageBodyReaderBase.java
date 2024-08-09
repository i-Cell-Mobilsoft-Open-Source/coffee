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
package hu.icellmobilsoft.coffee.rest.validation.xml;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;

import org.apache.commons.lang3.ArrayUtils;

import hu.icellmobilsoft.coffee.cdi.annotation.xml.ValidateXML;
import hu.icellmobilsoft.coffee.cdi.annotation.xml.ValidateXMLs;
import hu.icellmobilsoft.coffee.rest.validation.xml.exception.BaseProcessingExceptionWrapper;
import hu.icellmobilsoft.coffee.rest.validation.xml.reader.IRequestVersionReader;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;

/**
 * Ancestor implementation of JSON and XML payload deserialization and validation MessageBodyReader.
 * It executes only when the REST service method's request body parameter includes the @ValidateXML annotation.<br>
 * <br>
 * Full instructions for use: /docs/howto/xsd_xml_validation_depend_on_version.adoc<br>
 * <br>
 * Example of an extension:<br>
 *
 * <pre>
 * &#64;Provider
 * &#64;Consumes({ MediaType.APPLICATION_XML, MediaType.TEXT_XML })
 * &#64;Priority(Priorities.ENTITY_CODER)
 * public class XMLRequestMessageBodyReader extends XmlMessageBodyReaderBase&lt;BasicRequestType&gt; {
 * }
 * </pre>
 *
 * <br>
 *
 * Then annotate the endpoint.<br>
 *
 * @param <T>
 *            message body type
 *
 * @see ValidateXMLs
 * @see ValidateXML
 * @author attila.nyers
 * @author ferenc.lutischan
 * @author imre.scheffer
 * @since 1.0.0
 */
public abstract class XmlMessageBodyReaderBase<T> implements MessageBodyReader<T> {

    @Inject
    private JaxbTool jaxbTool;

    @Inject
    private IRequestVersionReader requestVersionReader;

    /**
     * Default constructor, constructs a new object.
     */
    public XmlMessageBodyReaderBase() {
        super();
    }

    /**
     * {@inheritDoc}
     *
     * Can XSD validation be performed?
     */
    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return ArrayUtils.isNotEmpty(getValidateIfPresent(annotations));
    }

    /**
     * {@inheritDoc}
     *
     * If there is a ValidateXML annotation, then it uses that.
     */
    @Override
    public T readFrom(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
            InputStream entityStream) {
        try {
            ValidateXML[] validates = getValidateIfPresent(annotations);
            String requestVersion = requestVersionReader.readVersion(entityStream);
            return jaxbTool.unmarshalXML(type, entityStream, requestVersion, validates);
        } catch (BaseException e) {
            throw new BaseProcessingExceptionWrapper(e);
        }
    }

    /**
     * Returns all {@link ValidateXML} annotations from given array of {@link Annotation}s.
     *
     * @param annotations
     *            array of {@code Annotation}s to search in
     * @return array of {@link ValidateXML} annotations, can be empty
     */
    public static ValidateXML[] getValidateIfPresent(Annotation[] annotations) {
        List<ValidateXML> list = new ArrayList<>();
        if (annotations != null) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof ValidateXMLs) {
                    return ((ValidateXMLs) annotation).value();
                } else if (annotation instanceof ValidateXML) {
                    list.add((ValidateXML) annotation);
                }
            }
        }
        return list.toArray(new ValidateXML[0]);
    }
}
