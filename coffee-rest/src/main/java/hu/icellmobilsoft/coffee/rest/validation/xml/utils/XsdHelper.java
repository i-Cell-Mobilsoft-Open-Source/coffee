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
package hu.icellmobilsoft.coffee.rest.validation.xml.utils;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.dto.exception.InvalidParameterException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.rest.validation.xml.exception.XsdProcessingException;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;

/**
 * XSD Helper
 *
 * @author ferenc.lutischan
 * @since 1.0.0
 */
@Dependent
public class XsdHelper implements IXsdHelper {

    private static final Map<String, Schema> xsdCache = new ConcurrentHashMap<>();
    private static final Map<String, JAXBContext> jaxbContextCache = new ConcurrentHashMap<>();

    @Inject
    @ThisLogger
    private AppLogger log;

    /**
     * Default constructor, constructs a new object.
     */
    public XsdHelper() {
        super();
    }

    /**
     * {@inheritDoc}
     *
     * Creates and caches JAXBContext for the class
     */
    @Override
    public JAXBContext getJAXBContext(Class<?> forClass) throws JAXBException, BaseException {
        if (forClass == null) {
            throw new InvalidParameterException("forClass is null!");
        }
        String className = forClass.getName();
        if (jaxbContextCache.containsKey(className)) {
            return jaxbContextCache.get(className);
        } else {
            JAXBContext jaxbContext = JAXBContext.newInstance(forClass);
            jaxbContextCache.put(className, jaxbContext);
            return jaxbContext;
        }
    }

    /**
     * {@inheritDoc}
     *
     * Creates and caches JAXBContext for the classes
     */
    @Override
    public JAXBContext getJAXBContext(Class<?>... forClasses) throws JAXBException, BaseException {
        if (forClasses == null) {
            throw new InvalidParameterException("forClasses is null!");
        }
        String className = Arrays.stream(forClasses).filter(Objects::nonNull).map(Class::getName).sorted().collect(Collectors.joining());
        if (jaxbContextCache.containsKey(className)) {
            return jaxbContextCache.get(className);
        } else {
            JAXBContext jaxbContext = JAXBContext.newInstance(forClasses);
            jaxbContextCache.put(className, jaxbContext);
            return jaxbContext;
        }
    }

    /**
     * {@inheritDoc}
     *
     * Creates and caches a {@link Schema} for the given XSD
     */
    @Override
    public Schema getSchema(String xsd, LSResourceResolver lsResourceResolver) throws BaseException, SAXException {
        // In this case, the XSD files need to be located somewhere on the JBoss server
        // if you want to package them into the WAR file. You'll likely need another classloader (possibly pass it through a class)
        // to ensure they are accessible and can be loaded correctly.
        if (StringUtils.isBlank(xsd) || lsResourceResolver == null) {
            throw new InvalidParameterException("xsd is blank or lsResourceResolver is null!");
        }
        Schema schema = xsdCache.get(xsd);
        if (schema == null) {
            InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(xsd);
            if (stream == null) {
                throw new XsdProcessingException(CoffeeFaultType.OPERATION_FAILED, "cannot find schema to validate");
            }
            StreamSource src = new StreamSource(stream);
            SchemaFactory sf = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
            try {
                sf.setResourceResolver(lsResourceResolver);
            } catch (Exception e) {
                throw new XsdProcessingException(CoffeeFaultType.OPERATION_FAILED, e.getMessage(), e);
            }
            schema = sf.newSchema(src);
            xsdCache.put(xsd, schema);
        } else {
            log.debug("xsd loaded from cache: {0}", xsd);
        }
        return schema;
    }

    /**
     * Clearing the underlying XSD schema cache {@link XsdHelper#xsdCache}
     */
    public void clearXsdCache() {
        xsdCache.clear();
    }

    /**
     * Clearing the underlying jaxb context cache {@link XsdHelper#jaxbContextCache}
     */
    public void clearJaxbContextCache() {
        jaxbContextCache.clear();
    }

}
