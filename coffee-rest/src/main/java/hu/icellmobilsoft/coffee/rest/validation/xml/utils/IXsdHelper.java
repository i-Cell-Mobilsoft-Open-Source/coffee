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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.validation.Schema;

import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;

import hu.icellmobilsoft.coffee.rest.validation.xml.exception.XsdProcessingException;

/**
 * Segéd xsd függvényekhez
 *
 * @see XsdHelper
 * @author ferenc.lutischan
 * @since 1.0.0
 */
public interface IXsdHelper {
    /**
     * <p>getJAXBContext.</p>
     */
    JAXBContext getJAXBContext(Class<?> forClass) throws JAXBException, XsdProcessingException;

    /**
     * <p>getSchema.</p>
     */
    Schema getSchema(String xsd, LSResourceResolver lsResourceResolver) throws XsdProcessingException, SAXException;
}
