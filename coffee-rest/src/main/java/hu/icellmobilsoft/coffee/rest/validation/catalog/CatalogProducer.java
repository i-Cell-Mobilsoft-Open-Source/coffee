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

import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Model;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.xml.catalog.Catalog;
import javax.xml.catalog.CatalogFeatures;
import javax.xml.catalog.CatalogManager;

import hu.icellmobilsoft.coffee.cdi.config.IConfigKey;
import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.rest.configuration.ApplicationConfiguration;
import hu.icellmobilsoft.coffee.rest.validation.xml.exception.XsdProcessingException;

/**
 * Producer Catalog-hoz, hogy CDI injektálható legyen
 *
 * @author mark.petrenyi
 * @author imre.scheffer
 * @since 1.0.0
 */
@Model
public class CatalogProducer {

    @Inject
    @ThisLogger
    private AppLogger log;

    @Inject
    private ApplicationConfiguration applicationConfiguration;

    @Any
    @Inject
    private Instance<ICatalogRegistry> registries;

    /**
     * Producer for @Inject Catalog feature
     *
     * @throws BaseException
     */
    @Produces
    public List<Catalog> publicCatalogResolver() throws BaseException {
        List<Catalog> catalogs = new ArrayList<Catalog>();
        List<String> paths = findCatalogPaths();

        Optional<String> xmlCatalogPath = applicationConfiguration.getOptionalString(IConfigKey.CATALOG_XML_PATH);
        String path = xmlCatalogPath
                .orElseThrow(() -> new TechnicalException(MessageFormat.format("The config of [{0}] not found!", IConfigKey.CATALOG_XML_PATH)));
        paths.add(path);

        for (String catalogPath : paths) {
            try {
                URI catalogUri = CatalogProducer.class.getClassLoader().getResource(catalogPath).toURI();

                catalogs.add(CatalogManager.catalog(CatalogFeatures.defaults(), catalogUri));
                log.debug("Catalog [{0}] added!", catalogUri);
            } catch (Exception e) {
                String msg = MessageFormat.format("Can not resolve catalog:[{0}], [{1}]", catalogPath, e.getLocalizedMessage());
                log.error(msg, e);
                throw new XsdProcessingException(CoffeeFaultType.OPERATION_FAILED, msg, e);
            }
            }
        return catalogs;
    }

    private List<String> findCatalogPaths() throws BaseException {
        List<String> list = new ArrayList<String>();
        for (ICatalogRegistry cr : registries) {
            log.info("Catalog registry found: [{0}]", cr.getClass().getName());
            List<String> pathList = cr.getSchemaCatalogList();
            for (String path : pathList) {
                if (!list.contains(path)) {
                    log.info("Registered catalog path found: [{0}]", path);
                    list.add(path);
                    }
                }
        }
        return list;
    }
}
