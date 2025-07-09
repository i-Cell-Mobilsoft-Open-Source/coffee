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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Model;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.UnmarshalException;
import jakarta.xml.bind.Unmarshaller;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;

import hu.icellmobilsoft.coffee.cdi.annotation.xml.ValidateXML;
import hu.icellmobilsoft.coffee.dto.exception.InvalidParameterException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.rest.validation.xml.error.IXsdValidationErrorCollector;
import hu.icellmobilsoft.coffee.rest.validation.xml.exception.XsdProcessingException;
import hu.icellmobilsoft.coffee.rest.validation.xml.utils.IXsdHelper;
import hu.icellmobilsoft.coffee.rest.validation.xml.utils.IXsdResourceResolver;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.se.api.exception.TechnicalException;
import hu.icellmobilsoft.coffee.tool.utils.annotation.RangeUtil;

/**
 * JAXB (un)marshaller and JAXB-related operations
 *
 * @author attila.nyers
 * @author ferenc.lutischan
 * @author imre.scheffer
 * @author m.petrenyi
 * @author balazs.joo
 * @since 1.0.0
 */
@Model
public class JaxbTool {

    private static final String ERR_MSG_TYPE_OR_BINARY_IS_NULL_OR_EMPTY = "type or binary is null or empty!";

    Instance<IXsdResourceResolver> resourceResolverInstance;
    IXsdResourceResolver resourceResolver;

    /**
     * Default constructor, constructs a new object.
     */
    public JaxbTool() {
        super();
    }

    /**
     * Deserialize the object and validate it against the specified XSD schema.
     *
     * @param <T>
     *            Returning type
     * @param type
     *            What type does the incoming data fit into?
     * @param entityStream
     *            The incoming stream to be processed
     * @param requestVersion
     *            The previously determined requestVersion, based on which the validation can be performed.
     * @param validateXMLs
     *            Annotations related to XSD validation
     * @return The result object
     * @throws BaseException
     *             In case of invalid input, or if the input data cannot be processed
     *
     */
    public <T> T unmarshalXML(Class<T> type, InputStream entityStream, String requestVersion, ValidateXML[] validateXMLs) throws BaseException {
        String schemaPath = getXsdPath(validateXMLs, requestVersion);
        // If it receives an empty schemaPath, it doesn't validate but simply converts it into an object
        return unmarshalXML(type, entityStream, schemaPath);
    }

    /**
     * Deserialize the object and validate it against the specified XSD schema.
     *
     * @param <T>
     *            Returning type
     * @param type
     *            What type does the incoming data fit into?
     * @param binary
     *            The incoming binary data to be processed
     * @param requestVersion
     *            The previously determined requestVersion, based on which the validation can be performed.
     * @param validateXMLs
     *            Annotations related to XSD validation
     * @return The result object
     * @throws BaseException
     *             In case of invalid input, or if the input data cannot be processed
     */
    public <T> T unmarshalXML(Class<T> type, byte[] binary, String requestVersion, ValidateXML[] validateXMLs) throws BaseException {
        if (Objects.isNull(type) || ArrayUtils.isEmpty(binary)) {
            throw new InvalidParameterException(ERR_MSG_TYPE_OR_BINARY_IS_NULL_OR_EMPTY);
        }
        ByteArrayInputStream inputStream = new ByteArrayInputStream(binary);
        return unmarshalXML(type, inputStream, requestVersion, validateXMLs);
    }

    /**
     * Converts XML into an object without schema validation.
     *
     * @param <T>
     *            Returning type
     * @param type
     *            Type of class representing an XML object
     * @param inputStream
     *            Stream containing the input data
     * @return An object corresponding to the XML with the read values.
     * @throws BaseException
     *             In case of invalid input, or if the input data cannot be processed
     */
    public <T> T unmarshalXML(Class<T> type, InputStream inputStream) throws BaseException {
        return unmarshalXML(type, inputStream, (String) null);
    }

    /**
     * Converts XML into an object without schema validation.
     *
     * @param <T>
     *            Returning type
     * @param type
     *            Type of class representing an XML object
     * @param binary
     *            Binary data containing the input information
     * @return An object corresponding to the XML with the read values.
     * @throws BaseException
     *             In case of invalid input, or if the input data cannot be processed
     */
    public <T> T unmarshalXML(Class<T> type, byte[] binary) throws BaseException {
        if (Objects.isNull(type) || ArrayUtils.isEmpty(binary)) {
            throw new InvalidParameterException(ERR_MSG_TYPE_OR_BINARY_IS_NULL_OR_EMPTY);
        }
        ByteArrayInputStream inputStream = new ByteArrayInputStream(binary);
        return unmarshalXML(type, inputStream);
    }

    /**
     * Converting XML into an object with schema validation.<br>
     * <br>
     * If necessary, it is possible to customize unmarshalling requirements using the "feature" or "properties" settings<br>
     * An example of security settings:<br>
     * https://owasp.org/www-project-cheat-sheets/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.html#JAXB_Unmarshaller <br>
     * <br>
     * We usually do this most commonly through the Xerces configurations:
     *
     * <pre>
     * -Dorg.xml.sax.parser=com.sun.org.apache.xerces.internal.parsers.SAXParser
     * -Djavax.xml.parsers.DocumentBuilderFactory=com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl
     * -Djavax.xml.parsers.SAXParserFactory=com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl
     * -DentityExpansionLimit=100
     * </pre>
     *
     * Additional settings can be configured as follows:<br>
     * https://docs.oracle.com/javase/7/docs/api/javax/xml/bind/Unmarshaller.html
     *
     * <pre>
     * SAXParserFactory spf = SAXParserFactory.newInstance();
     * spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
     * spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
     * spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
     * SAXParser saxParser = spf.newSAXParser();
     * saxParser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", "http://....");
     * Source xmlSource = new SAXSource(saxParser.getXMLReader(), new InputSource(inputStream));
     * T result = (T) unmarshaller.unmarshal(inputStream);
     * </pre>
     *
     * @param <T>
     *            Returning type
     * @param type
     *            Type of class representing an XML object
     * @param inputStream
     *            A stream containing the input data
     * @param schemaPath
     *            Schema path
     * @return An object corresponding to the XML with the read values.
     * @throws BaseException
     *             In case of invalid input, or if the input data cannot be processed
     */
    public <T> T unmarshalXML(Class<T> type, InputStream inputStream, String schemaPath) throws BaseException {
        if (type == null || inputStream == null) {
            throw new InvalidParameterException("type or inputStream is null!");
        }
        Instance<IXsdValidationErrorCollector> errorCollectorInstance = null;
        Instance<IXsdHelper> xsdHelperInstance = null;
        IXsdHelper xsdHelper = null;
        IXsdValidationErrorCollector errorCollector = null;
        try {
            xsdHelperInstance = CDI.current().select(IXsdHelper.class);
            xsdHelper = createDependentCDIInstance(xsdHelperInstance);
            JAXBContext jaxbContext = xsdHelper.getJAXBContext(type);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            errorCollectorInstance = CDI.current().select(IXsdValidationErrorCollector.class);
            errorCollector = createDependentCDIInstance(errorCollectorInstance);
            unmarshaller.setEventHandler(errorCollector);
            // if schemaPath is empty -> no validation, only conversion
            if (StringUtils.isNotBlank(schemaPath)) {
                unmarshaller.setSchema(xsdHelper.getSchema(schemaPath, createLSResourceResolverInstance(schemaPath)));
            }
            @SuppressWarnings("unchecked")
            T result = (T) unmarshaller.unmarshal(inputStream);
            if (!errorCollector.getErrors().isEmpty()) {
                throw new XsdProcessingException(errorCollector.getErrors(), null);
            }

            return result;
        } catch (UnmarshalException e) {
            // The default parser terminates the process on the first FATAL_ERROR with an exception throw.
            // This behavior can be set using spf.setFeature ("http://apache.org/xml/features/continue-after-fatal-error", true)
            // You can achieve this behavior using the setFeature method, but it currently meets our needs. However, the errors detected so far must
            // be included in the exception.
            throw new XsdProcessingException(errorCollector.getErrors(), e);
        } catch (JAXBException | SAXException e) {
            throw new XsdProcessingException(CoffeeFaultType.INVALID_INPUT, e.getLocalizedMessage(), e);
        } finally {
            if (xsdHelperInstance != null && xsdHelper != null) {
                xsdHelperInstance.destroy(xsdHelper);
            }
            if (errorCollectorInstance != null && errorCollector != null) {
                errorCollectorInstance.destroy(errorCollector);
            }
            if (resourceResolverInstance != null && resourceResolver != null) {
                resourceResolverInstance.destroy(resourceResolver);
            }
        }
    }

    /**
     * Converting XML into an object with schema validation.
     *
     * @param <T>
     *            Returning type
     * @param type
     *            Type of class representing an XML object
     * @param binary
     *            The binary data containing the input
     * @param schemaPath
     *            Schema path
     * @return An object corresponding to the XML with the read values.
     * @throws BaseException
     *             In case of invalid input, or if the input data cannot be processed
     */
    public <T> T unmarshalXML(Class<T> type, byte[] binary, String schemaPath) throws BaseException {
        if (Objects.isNull(type) || ArrayUtils.isEmpty(binary)) {
            throw new InvalidParameterException(ERR_MSG_TYPE_OR_BINARY_IS_NULL_OR_EMPTY);
        }
        ByteArrayInputStream inputStream = new ByteArrayInputStream(binary);
        return unmarshalXML(type, inputStream, schemaPath);
    }

    /**
     * Marshals given XML {@link Object} to {@link String}. <br>
     * Sets the following fix parameters for the conversion:
     * <ul>
     * <li>jaxb.formatted.output - TRUE ({@link Marshaller#JAXB_FORMATTED_OUTPUT})</li>
     * <li>jaxb.fragment - TRUE ({@link Marshaller#JAXB_FRAGMENT})</li>
     * </ul>
     *
     * @param obj
     *            XML {@code Object}
     * @param schemaPath
     *            path to XSD or catalog to validate on, if null, then validation is not executed
     * @return XML String
     * @throws BaseException
     *             if invalid input or cannot be marshalled
     */
    public String marshalXML(Object obj, String schemaPath) throws BaseException {
        Map<String, Object> properties = new HashMap<>();
        properties.put(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        properties.put(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
        return marshalXML(obj, schemaPath, properties);
    }

    /**
     * Marshals given XML {@link Object} to {@link String}. <br>
     * Sets the following fix parameters for the conversion:
     * <ul>
     * <li>jaxb.formatted.output - TRUE ({@link Marshaller#JAXB_FORMATTED_OUTPUT})</li>
     * <li>jaxb.fragment - TRUE ({@link Marshaller#JAXB_FRAGMENT})</li>
     * </ul>
     *
     * @param obj
     *            XML {@code Object}
     * @param schemaPath
     *            path to XSD or catalog to validate on, if null, then validation is not executed
     * @return XML String
     * @param additionalClasses
     *            these classes will be added to the {@link JAXBContext}. Typically in case of 'Class not known to this context' errors.
     * @throws BaseException
     *             if invalid input or cannot be marshalled
     */
    public String marshalXML(Object obj, String schemaPath, Class<?>... additionalClasses) throws BaseException {
        Map<String, Object> properties = new HashMap<>();
        properties.put(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        properties.put(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
        return marshalXML(obj, schemaPath, properties, additionalClasses);
    }

    /**
     * Marshals given XML {@link Object} to {@link String}. <br>
     * Sets the following fix parameters for the conversion:
     * <ul>
     * <li>jaxb.formatted.output - TRUE ({@link Marshaller#JAXB_FORMATTED_OUTPUT})</li>
     * <li>jaxb.fragment - TRUE ({@link Marshaller#JAXB_FRAGMENT})</li>
     * </ul>
     *
     * @param obj
     *            XML {@code Object}
     * @param schemaPath
     *            path to XSD or catalog file to validate on, if null, then validation is not executed
     * @param marshallerProperties
     *            marshaller properties
     * @return XML String
     * @throws BaseException
     *             if invalid input or cannot be marshalled
     */
    public String marshalXML(Object obj, String schemaPath, Map<String, Object> marshallerProperties) throws BaseException {
        return marshalXML(obj, schemaPath, marshallerProperties, (Class<?>[]) null);
    }

    /**
     *
     * Marshals given XML {@link Object} to {@link String} with given parameters.
     *
     * @param obj
     *            XML {@code Object}
     * @param schemaPath
     *            path to XSD or catalog to validate on, if null, then validation is not executed (possibly it should be an xml catalog file)
     * @param marshallerProperties
     *            marshaller properties
     * @param additionalClasses
     *            these classes will be added to the {@link JAXBContext}. Typically in case of 'Class not known to this context' errors.
     * @return XML String
     * @throws BaseException
     *             if invalid input or cannot be marshalled
     */
    public String marshalXML(Object obj, String schemaPath, Map<String, Object> marshallerProperties, Class<?>... additionalClasses)
            throws BaseException {
        if (obj == null) {
            throw new InvalidParameterException("obj is null!");
        }
        Instance<IXsdValidationErrorCollector> errorCollectorInstance = null;
        Instance<IXsdHelper> xsdHelperInstance = null;
        IXsdHelper xsdHelper = null;
        IXsdValidationErrorCollector errorCollector = null;
        try {
            xsdHelperInstance = CDI.current().select(IXsdHelper.class);
            xsdHelper = createDependentCDIInstance(xsdHelperInstance);
            JAXBContext jaxbContext;
            if (additionalClasses != null && additionalClasses.length != 0) {
                List<Class<?>> contextClasses = new ArrayList<>(Arrays.asList(additionalClasses));
                contextClasses.add(obj.getClass());
                jaxbContext = xsdHelper.getJAXBContext(contextClasses.toArray(new Class<?>[0]));
            } else {
                jaxbContext = xsdHelper.getJAXBContext(obj.getClass());
            }
            Marshaller marshaller = jaxbContext.createMarshaller();
            if (marshallerProperties != null) {
                for (Entry<String, Object> entry : marshallerProperties.entrySet()) {
                    marshaller.setProperty(entry.getKey(), entry.getValue());
                }
            }
            errorCollectorInstance = CDI.current().select(IXsdValidationErrorCollector.class);
            errorCollector = createDependentCDIInstance(errorCollectorInstance);
            marshaller.setEventHandler(errorCollector);
            // if schemaPath is empty -> no validation, only conversion
            if (StringUtils.isNotBlank(schemaPath)) {
                marshaller.setSchema(xsdHelper.getSchema(schemaPath, createLSResourceResolverInstance(schemaPath)));
            }
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(obj, stringWriter);
            if (!errorCollector.getErrors().isEmpty()) {
                throw new XsdProcessingException(errorCollector.getErrors(), null);
            }
            return stringWriter.getBuffer().toString();
        } catch (JAXBException | SAXException e) {
            throw new XsdProcessingException(CoffeeFaultType.INVALID_INPUT, e.getMessage(), e);
        } finally {
            if (xsdHelperInstance != null && xsdHelper != null) {
                xsdHelperInstance.destroy(xsdHelper);
            }
            if (errorCollectorInstance != null && errorCollector != null) {
                errorCollectorInstance.destroy(errorCollector);
            }
            if (resourceResolverInstance != null && resourceResolver != null) {
                resourceResolverInstance.destroy(resourceResolver);
            }
        }
    }

    /**
     * Returns an instance of {@link LSResourceResolver}.<br>
     * This allows overriding it to use your own {@code LSResourceResolver} implementation for locating XSDs.<br>
     * To override this, you need to implement {@link IXsdResourceResolver} with {@code @Alternative} and {@code @Model} annotations.<br>
     * Also, you need to register it in the beans.xml as an alternative class.<br>
     * Related documentation: /docs/howto/xsd_xml_validation_depend_on_version.adoc
     *
     * @param schemaPath
     *            Schema path
     * @return Instance of a new {@code LSResourceResolver} implementation
     * @throws BaseException
     *             if any CDI error occurs
     */
    protected LSResourceResolver createLSResourceResolverInstance(String schemaPath) throws BaseException {
        resourceResolverInstance = CDI.current().select(IXsdResourceResolver.class);
        resourceResolver = createDependentCDIInstance(resourceResolverInstance);
        resourceResolver.setXsdDirPath(schemaPath);
        return resourceResolver;
    }

    /**
     * Obtaining a method parameter annotated with ValidateXML
     *
     * @param validateXMLs
     *            Annotations of ValidateXML on REST method parameters
     * @param requestVersion
     *            searched version
     * @return defined XSD path
     * @throws BaseException
     *             if invalid input or cannot read xsd path
     */
    public String getXsdPath(ValidateXML[] validateXMLs, String requestVersion) throws BaseException {
        if (validateXMLs == null) {
            throw new InvalidParameterException("validateXMLs is null!");
        }
        for (ValidateXML versionValidate : validateXMLs) {
            if (versionValidate.version().include().length == 0 // No version specified
                    || RangeUtil.inRanges(versionValidate.version().include(), requestVersion)) {
                return versionValidate.xsdPath();
            }
        }
        throw new XsdProcessingException(CoffeeFaultType.INVALID_INPUT, "Invalid XML requestVersion: " + requestVersion);
    }

    /**
     * CDI util
     *
     * @param <I>
     *            class type
     * @param instance
     *            the instance to use the destroy later on
     * @return the resolved bean
     * @throws BaseException
     *             if any CDI error occurs
     */
    protected static <I> I createDependentCDIInstance(Instance<I> instance) throws BaseException {
        if (instance == null) {
            throw new TechnicalException(
                    hu.icellmobilsoft.coffee.se.api.exception.enums.CoffeeFaultType.OPERATION_FAILED,
                    "CDI instance cannot be null");
        }
        if (instance.isResolvable()) {
            return instance.get();
        }
        throw new TechnicalException(hu.icellmobilsoft.coffee.se.api.exception.enums.CoffeeFaultType.OPERATION_FAILED, "Bean not resolvable");
    }

}
