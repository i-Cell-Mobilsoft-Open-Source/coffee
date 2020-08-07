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
package hu.icellmobilsoft.coffee.rest.validation.catalog;

import java.io.StringReader;
import java.util.Iterator;

import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.xml.catalog.Catalog;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.InputSource;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.rest.validation.xml.utils.IXsdResourceResolver;

/**
 * Implements CatalogResolver. <br/>
 * This class implements a SAX EntityResolver, StAX XMLResolver, Schema Validation LSResourceResolver and Transform URIResolver. <br/>
 * For multi module projekt {@link Alternative} activation need own class like:
 * 
 * <pre>
 * &#64;Priority(100)
 * &#64;Alternative
 * public class ProjectPublicCatalogResolver extends PublicCatalogResolver {
 * }
 * </pre>
 *
 * @author imre.scheffer
 * @author mark.petrenyi
 * @see Inspired by javax.xml.catalog.CatalogResolverImpl, javax.xml.catalog.Util
 * @since 1.0.0
 */
@Alternative
public class PublicCatalogResolver implements LSResourceResolver, IXsdResourceResolver {

    @Inject
    private Catalog catalog;

    @Inject
    @ThisLogger
    private AppLogger log;

    /*
     * Implements the EntityResolver interface
     */
    public InputSource resolveEntity(String publicId, String systemId) {

        String resolvedSystemId = resolve(catalog, publicId, systemId);

        if (resolvedSystemId != null) {

            return new InputSource(resolvedSystemId);
        }

        // no action, allow the parser to continue
        return new InputSource(new StringReader(""));
    }

    private String resolve(Catalog catalog, String publicId, String systemId) {
        log.debug("Try resolve catalog with publicId:[{0}], systemId:[{1}]", publicId, systemId);
        String resolvedSystemId = null;

        if (systemId != null) {
            /*
             * If a system identifier is specified, it is used no matter how prefer is set.
             */
            resolvedSystemId = catalog.matchSystem(systemId);
        }

        if (resolvedSystemId == null && publicId != null) {
            resolvedSystemId = catalog.matchPublic(publicId);
        }

        if (resolvedSystemId == null && systemId != null) {
            resolvedSystemId = catalog.matchURI(systemId);
        }

        // search alternative catalogs
        if (resolvedSystemId == null) {
            Iterator<Catalog> iter = catalog.catalogs().iterator();
            while (iter.hasNext()) {
                resolvedSystemId = resolve(iter.next(), publicId, systemId);
                if (resolvedSystemId != null) {
                    break;
                }

            }
        }
        // Kikeressük classpath-ról a catalog által meghatározott xsd-t
        if (resolvedSystemId != null) {
            log.debug("Found resource with catalog resolvedSystemId:[{0}]", resolvedSystemId);
        } else {
            log.warn("Resource in catalog with publicId:[{0}], systemId:[{1}] not found!", publicId, systemId);
        }
        return resolvedSystemId;
    }

    /** {@inheritDoc} */
    @Override
    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
        InputSource is = resolveEntity(publicId != null ? publicId : namespaceURI, systemId);

        if (is != null && !is.isEmpty()) {
            return new CatalogLsInputImpl(is.getSystemId());
        }

        // no action, allow the parser to continue
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * A paraméterben kapott Xsd elérési útvonalat lementi egy belső változóba (ha szükséges).
     */
    @Override
    public void setXsdDirPath(String xsdDirPath) {
        // Cataloghoz ez nem kell
    }
}
