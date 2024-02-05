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
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
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

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.InvalidParameterException;
import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.cdi.annotation.xml.ValidateXML;
import hu.icellmobilsoft.coffee.rest.validation.xml.error.IXsdValidationErrorCollector;
import hu.icellmobilsoft.coffee.rest.validation.xml.exception.XsdProcessingException;
import hu.icellmobilsoft.coffee.rest.validation.xml.reader.IXmlRequestVersionReader;
import hu.icellmobilsoft.coffee.rest.validation.xml.utils.IXsdHelper;
import hu.icellmobilsoft.coffee.rest.validation.xml.utils.IXsdResourceResolver;
import hu.icellmobilsoft.coffee.tool.utils.annotation.RangeUtil;

/**
 * JAXB (un)marshaller es jaxb kapcsolatos muveletek
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

    /**
     * Default constructor, constructs a new object.
     */
    public JaxbTool() {
        super();
    }

    /**
     * Deszerializálja az objektumot, és validálja a megadott XSD séma alapján
     *
     * @param <T>
     *            Visszatérő típus
     * @param type
     *            Milyen típusba illeszkedik a bejövő adat
     * @param entityStream
     *            A feldolgozandó bemeneti folyam
     * @param validateXMLs
     *            Az XSD validációhoz kapcsolódó annotációk
     * @return A eredményobjektum
     * @throws BaseException
     *             érvénytelen bemenet esetén, vagy ha nem lehet feldolgozni a bemeneti adatot
     */
    public <T> T unmarshalXML(Class<T> type, InputStream entityStream, ValidateXML[] validateXMLs) throws BaseException {
        if (type == null || entityStream == null) {
            throw new InvalidParameterException("type or entityStream is null!");
        }
        String requestVersion = null;
        if (validateXMLs != null && Arrays.stream(validateXMLs).anyMatch(e -> e.version().include().length > 0)) {
            // ha van megadva version az annotacioban, akkor megprobaljuk kiszedni a 'requestVersion'-t a requestbol
            requestVersion = getRequestVersion(entityStream);
        }

        String schemaPath = getXsdPath(validateXMLs, requestVersion);
        // Ha nem kap csak üres schemaPath-ot nem validál, csak objektummá alakít:
        return unmarshalXML(type, entityStream, schemaPath);
    }

    /**
     * Deszerializálja az objektumot, és validálja a megadott XSD séma alapján
     *
     * @param <T>
     *            Visszatérő típus
     * @param type
     *            Milyen típusba illeszkedik a bejövő adat
     * @param binary
     *            A feldolgozandó bemeneti bináris
     * @param validateXMLs
     *            Az XSD validációhoz kapcsolódó annotációk
     * @return A eredményobjektum
     * @throws BaseException
     *             érvénytelen bemenet esetén, vagy ha nem lehet feldolgozni a bemeneti adatot
     */
    public <T> T unmarshalXML(Class<T> type, byte[] binary, ValidateXML[] validateXMLs) throws BaseException {
        if (Objects.isNull(type) || ArrayUtils.isEmpty(binary)) {
            throw new InvalidParameterException(ERR_MSG_TYPE_OR_BINARY_IS_NULL_OR_EMPTY);
        }
        ByteArrayInputStream inputStream = new ByteArrayInputStream(binary);
        return unmarshalXML(type, inputStream, validateXMLs);
    }

    /**
     * XML objektummá alakítás séma validáció nélkül.
     *
     * @param <T>
     *            visszatérő típus
     * @param type
     *            xml objektumot reprezentáló osztály típusa
     * @param inputStream
     *            a bemeneti adatokat tartalmazó folyam
     * @return xml-nek megfelelő objektum a felolvasott értékekkel.
     * @throws BaseException
     *             érvénytelen bemenet esetén, vagy ha nem lehet feldolgozni a bemeneti adatot
     */
    public <T> T unmarshalXML(Class<T> type, InputStream inputStream) throws BaseException {
        return unmarshalXML(type, inputStream, (String) null);
    }

    /**
     * XML objektummá alakítás séma validáció nélkül.
     *
     * @param <T>
     *            visszatérő típus
     * @param type
     *            xml objektumot reprezentáló osztály típusa
     * @param binary
     *            a bemeneti adatokat tartalmazó bináris
     * @return xml-nek megfelelő objektum a felolvasott értékekkel.
     * @throws BaseException
     *             érvénytelen bemenet esetén, vagy ha nem lehet feldolgozni a bemeneti adatot
     */
    public <T> T unmarshalXML(Class<T> type, byte[] binary) throws BaseException {
        if (Objects.isNull(type) || ArrayUtils.isEmpty(binary)) {
            throw new InvalidParameterException(ERR_MSG_TYPE_OR_BINARY_IS_NULL_OR_EMPTY);
        }
        ByteArrayInputStream inputStream = new ByteArrayInputStream(binary);
        return unmarshalXML(type, inputStream);
    }

    /**
     * XML objektummá alakítás séma validációval.<br>
     * <br>
     * Szukseg eseten lehetseges testre szabni a unmarshall igenyeket a "feature" vagy a "properties" beallitasokkal.<br>
     * Egy pelda a security beallitasokra: <br>
     * https://owasp.org/www-project-cheat-sheets/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.html#JAXB_Unmarshaller <br>
     * <br>
     * Mi ezt leggyakrabban a xerces konfigjain keresztul csinaljuk:
     *
     * <pre>
     * -Dorg.xml.sax.parser=com.sun.org.apache.xerces.internal.parsers.SAXParser
     * -Djavax.xml.parsers.DocumentBuilderFactory=com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl
     * -Djavax.xml.parsers.SAXParserFactory=com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl
     * -DentityExpansionLimit=100
     * </pre>
     *
     * Lehetoseg van tovabbi beallitasokra a kovetkezo minta szerint:<br>
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
     *            visszatérő típus
     * @param type
     *            xml objektumot reprezentáló osztály típusa
     * @param inputStream
     *            a bemeneti adatokat tartalmazó folyam
     * @param schemaPath
     *            séma elérési útja
     * @return xml-nek megfelelő objektum a felolvasott értékekkel.
     * @throws BaseException
     *             érvénytelen bemenet esetén, vagy ha nem lehet feldolgozni a bemeneti adatot
     */
    public <T> T unmarshalXML(Class<T> type, InputStream inputStream, String schemaPath) throws BaseException {
        if (type == null || inputStream == null) {
            throw new InvalidParameterException("type or inputStream is null!");
        }
        IXsdValidationErrorCollector errorCollector = createCDIInstance(IXsdValidationErrorCollector.class);
        try {
            IXsdHelper xsdHelper = createCDIInstance(IXsdHelper.class);
            JAXBContext jaxbContext = xsdHelper.getJAXBContext(type);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
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
            // a default parser az elso FATAL_ERROR-nal megszakitja a folyamatot exception dobassal.
            // Ezt a mukodest lehet allitani a spf.setFeature("http://apache.org/xml/features/continue-after-fatal-error", true)
            // feature beallitassal, de nekunk megfelel igy is. Viszont az addig eszlelt hibakat bele kell rakni az exceptionba
            throw new XsdProcessingException(errorCollector.getErrors(), e);
        } catch (JAXBException | SAXException e) {
            throw new XsdProcessingException(CoffeeFaultType.INVALID_INPUT, e.getLocalizedMessage(), e);
        }
    }

    /**
     * XML objektummá alakítás séma validációval.
     *
     * @param <T>
     *            visszatérő típus
     * @param type
     *            xml objektumot reprezentáló osztály típusa
     * @param binary
     *            a bemeneti adatokat tartalmazó bináris
     * @param schemaPath
     *            séma elérési útja
     * @return xml-nek megfelelő objektum a felolvasott értékekkel.
     * @throws BaseException
     *             érvénytelen bemenet esetén, vagy ha nem lehet feldolgozni a bemeneti adatot
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
     *            path to XSD to validate on, if null, then validation is not executed
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
     *
     * Marshals given XML {@link Object} to {@link String} with given parameters.
     *
     * @param obj
     *            XML {@code Object}
     * @param schemaPath
     *            path to XSD to validate on, if null, then validation is not executed
     * @param marshallerProperties
     *            marshaller properties
     * @return XML String
     * @throws BaseException
     *             if invalid input or cannot be marshalled
     */
    public String marshalXML(Object obj, String schemaPath, Map<String, Object> marshallerProperties) throws BaseException {
        if (obj == null) {
            throw new InvalidParameterException("obj is null!");
        }
        try {
            IXsdHelper xsdHelper = createCDIInstance(IXsdHelper.class);
            JAXBContext jaxbContext = xsdHelper.getJAXBContext(obj.getClass());
            Marshaller marshaller = jaxbContext.createMarshaller();
            if (marshallerProperties != null) {
                for (Entry<String, Object> entry : marshallerProperties.entrySet()) {
                    marshaller.setProperty(entry.getKey(), entry.getValue());
                }
            }
            IXsdValidationErrorCollector errorCollector = createCDIInstance(IXsdValidationErrorCollector.class);
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
        }
    }

    /**
     * Visszatér egy {@link LSResourceResolver} példánnyal.<br>
     * Így lehetővé válik, hogy ennek a felülírásával saját {@code LSResourceResolver} implementációt alkalmazzunk az XSD-k megtalálására.<br>
     * A felülíráshoz a {@link IXsdResourceResolver}-t kell implementálni (@Alternative és @Modell annotációval).<br>
     * Valamint a {@code beans.xml}-be be kell jegyezni, mint alternative class-t.<br>
     * Kapcsolódó dokumentáció: /docs/howto/xsd_xml_validation_depend_on_version.adoc
     *
     * @param schemaPath
     *            séma elérési útja
     * @return Új {@code LSResourceResolver} implementáció példánya
     */
    protected LSResourceResolver createLSResourceResolverInstance(String schemaPath) {
        IXsdResourceResolver resourceResolver = createCDIInstance(IXsdResourceResolver.class);

        resourceResolver.setXsdDirPath(schemaPath);

        return resourceResolver;
    }

    /**
     * Vissza adja request version-t. Nem érdemes felülírni, helyette a CDI lehetőségeit kell használni, mégpedig implementálni a
     * IXmlRequestVersionReader osztályt és alternative-ként aktiválni
     *
     * @param entityStream
     *            http REST entity
     * @return request version
     * @throws BaseException
     *             if invalid input or cannot read request version
     */
    public String getRequestVersion(InputStream entityStream) throws BaseException {
        if (entityStream == null) {
            throw new InvalidParameterException("entityStream is null!");
        }
        try {
            entityStream.mark(0);
            String requestVersion = createCDIInstance(IXmlRequestVersionReader.class).readFromXML(entityStream);
            entityStream.reset();
            return requestVersion;
        } catch (IOException | TechnicalException e) {
            throw new XsdProcessingException(CoffeeFaultType.INVALID_INPUT, "Error reading XML requestVersion", e);
        }
    }

    /**
     * ValidateXML annotációval ellátott metódus paraméter megszerzése
     *
     * @param validateXMLs
     *            ValidateXML annotációk REST metódus paraméterén
     * @param requestVersion
     *            keresett verzió
     * @return definiált XSD path
     * @throws BaseException
     *             if invalid input or cannot read xsd path
     */
    public String getXsdPath(ValidateXML[] validateXMLs, String requestVersion) throws BaseException {
        if (validateXMLs == null) {
            throw new InvalidParameterException("validateXMLs is null!");
        }
        for (ValidateXML versionValidate : validateXMLs) {
            if (versionValidate.version().include().length == 0 // Nincs version megadva
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
     * @param type
     *            class
     * @return class
     */
    protected static <I> I createCDIInstance(Class<I> type) {
        CDI<Object> cdi = CDI.current();
        Instance<I> instance = cdi.select(type);
        I result = instance.get();
        cdi.destroy(instance);

        return result;
    }
}
