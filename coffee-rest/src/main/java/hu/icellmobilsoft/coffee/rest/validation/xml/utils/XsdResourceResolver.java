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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

import jakarta.enterprise.inject.Alternative;

import org.apache.commons.lang3.StringUtils;
import org.apache.deltaspike.core.util.PropertyFileUtils;
import org.w3c.dom.DOMImplementationSource;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * XSD schemalocation based resource resolver.<br>
 * This class implements a SAX EntityResolver, StAX XMLResolver, Schema Validation LSResourceResolver and Transform URIResolver. <br>
 * For multi module projekt {@link Alternative} activation need own class like:
 * 
 * <pre>
 * &#64;Priority(100)
 * &#64;Alternative
 * public class ProjectXsdResourceResolver extends XsdResourceResolver {
 * }
 * </pre>
 *
 * @author cstamas
 * @author robert.kaplar
 * @since 1.0.0
 */
@Alternative
public class XsdResourceResolver implements LSResourceResolver, IXsdResourceResolver {
    private static final Logger log = Logger.getLogger(XsdResourceResolver.class);
    private String xsdDirPath;

    /**
     * Default constructor, constructs a new object.
     */
    public XsdResourceResolver() {
        super();
    }

    /**
     * {@inheritDoc}
     *
     * Xsd feloldás
     */
    @Override
    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
        if (StringUtils.isBlank(systemId)) {
            return null;
        }
        DOMImplementationLS domImplLS = getDOMImplementationLS();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        final LSInput input = domImplLS.createLSInput();

        log.trace("0: resolving: [{0}], on base path [{1}]", systemId, xsdDirPath);
        InputStream resStream = null; // classLoader.getResourceAsStream(s);
        if (resStream == null) {
            // xsdDirPath = "xsd/eu/ss/swarm/sample/dto/user/user.xsd"
            // systemId = "../common/common.xsd"
            String temp = StringUtils.substringBeforeLast(xsdDirPath, "/");
            String temp2 = systemId;
            if (StringUtils.contains(systemId, "..")) {
                // temp = "xsd/eu/ss/swarm/sample/dto/user"
                temp = StringUtils.substringBeforeLast(temp, "/");
                // temp = "xsd/eu/ss/swarm/sample/dto"
                temp2 = StringUtils.substringAfter(systemId, "..");
                // temp2 = "/common/common.xsd"
            } else {
                // hogyha sajat konyvtarban van az import, vissza kell rakni a levagott separatort
                temp = temp + "/";
            }
            String path = temp + temp2;
            resStream = classLoader.getResourceAsStream(path);
            log.trace("1: finding path: [{0}], resStream [{1}]", path, resStream);
            if (resStream == null) {
                try {
                    Enumeration<URL> urlEnum = PropertyFileUtils.resolvePropertyFiles(path);
                    log.trace("2: deltaspike PropertyFileUtils found xsd file [{0}]", urlEnum.hasMoreElements());
                    if (urlEnum.hasMoreElements()) {
                        resStream = urlEnum.nextElement().openStream();
                        log.trace("2.1: finding path: [{0}], resStream [{1}]", path, resStream);
                    }
                } catch (IOException e) {
                    log.trace("2.2: {0}", e.getLocalizedMessage());
                }
            }
        }
        if (resStream == null) {
            // xsdDirPath = "xsd/eu/ss/swarm/sample/dto/user/user.xsd"
            // systemId =
            // "../../../../../../../../../../target/unpacked-files/coffee-resources/xsd/eu/ss/coffee/swarm/dto/common/common.xsd"
            String temp = StringUtils.substringAfter(systemId, "coffee-resources/");
            // temp = "xsd/eu/ss/coffee/swarm/dto/common/common.xsd"
            String path = temp;
            if (StringUtils.isNotBlank(path)) {
                resStream = classLoader.getResourceAsStream(path);
                log.trace("3: finding path: [{0}], resStream [{1}]", path, resStream);
                if (resStream == null) {
                    try {
                        Enumeration<URL> urlEnum = PropertyFileUtils.resolvePropertyFiles(path);
                        log.trace("4: deltaspike PropertyFileUtils found xsd file [{0}]", urlEnum.hasMoreElements());
                        if (urlEnum.hasMoreElements()) {
                            resStream = urlEnum.nextElement().openStream();
                            log.trace("4.1: finding path: [{0}], resStream [{1}]", path, resStream);
                        }
                    } catch (IOException e) {
                        log.trace("4.2: {0}", e.getLocalizedMessage());
                    }
                }
            }
        }
        if (resStream == null) {
            log.warn("ResourceResolver: Resource [{0}] not found in classpaths!", systemId);
            return null;
        }
        input.setByteStream(resStream);
        input.setSystemId(systemId);
        return input;
    }

    private DOMImplementationLS getDOMImplementationLS() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            final DOMImplementationRegistry registry;
            registry = DOMImplementationRegistry.newInstance();
            return (DOMImplementationLS) registry.getDOMImplementation("LS");
        } catch (ClassNotFoundException cnfe) {
            // This is an ugly workaround of this bug: https://issues.jboss.org/browse/WFLY-4416
            try {
                Class<?> sysImpl = classLoader.loadClass("com.sun.org.apache.xerces.internal.dom.DOMXSImplementationSourceImpl");
                DOMImplementationSource source = (DOMImplementationSource) sysImpl.newInstance();
                return (DOMImplementationLS) source.getDOMImplementation("LS");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e1) {
            throw new RuntimeException(e1);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setXsdDirPath(String xsdDirPath) {
        this.xsdDirPath = xsdDirPath;
    }
}
