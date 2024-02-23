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
import java.util.Optional;

import javax.xml.catalog.Catalog;
import javax.xml.catalog.CatalogFeatures;
import javax.xml.catalog.CatalogManager;

import jakarta.enterprise.inject.Model;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

import hu.icellmobilsoft.coffee.cdi.config.IConfigKey;
import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.configuration.ApplicationConfiguration;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.exception.BaseException;
import hu.icellmobilsoft.coffee.exception.TechnicalException;
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

    /**
     * Default constructor, constructs a new object.
     */
    public CatalogProducer() {
        super();
    }

    /**
     * Producer for {@code @Inject {@link Catalog}} feature
     *
     * @return {@code Catalog} object
     * @throws BaseException
     *             exception
     */
    @Produces
    public Catalog publicCatalogResolver() throws BaseException {
        Optional<String> xmlCatalogPath = applicationConfiguration.getOptionalString(IConfigKey.CATALOG_XML_PATH);
        String path = xmlCatalogPath
                .orElseThrow(() -> new TechnicalException(MessageFormat.format("The config of [{0}] not found!", IConfigKey.CATALOG_XML_PATH)));
        try {
            URI catalogUri = Thread.currentThread().getContextClassLoader().getResource(path).toURI();

            return CatalogManager.catalog(CatalogFeatures.defaults(), catalogUri);
        } catch (Exception e) {
            String msg = MessageFormat.format("Can not resolve catalog:[{0}], [{1}]", path, e.getLocalizedMessage());
            log.error(msg, e);
            throw new XsdProcessingException(CoffeeFaultType.OPERATION_FAILED, msg, e);
        }
    }
}
