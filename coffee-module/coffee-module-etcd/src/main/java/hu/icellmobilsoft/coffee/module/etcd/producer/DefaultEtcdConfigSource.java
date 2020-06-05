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
package hu.icellmobilsoft.coffee.module.etcd.producer;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.jboss.logging.Logger;

import hu.icellmobilsoft.coffee.dto.exception.BONotFoundException;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.module.etcd.config.DefaultEtcdConfigImpl;
import hu.icellmobilsoft.coffee.module.etcd.handler.ConfigEtcdHandler;
import hu.icellmobilsoft.coffee.module.etcd.service.ConfigEtcdService;

/**
 * Microprofile-config implementation for configSource
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
public class DefaultEtcdConfigSource implements ConfigSource {

    private static Logger LOGGER = hu.icellmobilsoft.coffee.cdi.logger.LogProducer.getStaticLogger(DefaultEtcdConfigSource.class);

    /** {@inheritDoc} */
    @Override
    public int getOrdinal() {
        return 150;
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, String> getProperties() {
        try {
            CDI<Object> cdi = CDI.current();
            Instance<ConfigEtcdService> configEtcdServiceInstance = cdi.select(ConfigEtcdService.class);
            Map<String, String> values = configEtcdServiceInstance.get().getList();
            cdi.destroy(configEtcdServiceInstance);
            return values;
        } catch (BaseException e) {
            LOGGER.error(MessageFormat.format("Error in getting all values from ETCD: [{0}]", e.getLocalizedMessage()), e);
        }
        return Collections.emptyMap();
    }

    /** {@inheritDoc} */
    @Override
    public String getValue(String propertyName) {
        try {
            CDI<Object> cdi = CDI.current();
            Instance<ConfigEtcdHandler> configEtcdHandlerInstance = cdi.select(ConfigEtcdHandler.class);
            String value = configEtcdHandlerInstance.get().getValue(propertyName);
            cdi.destroy(configEtcdHandlerInstance);
            return value;
        } catch (BONotFoundException e) {
            LOGGER.debug(MessageFormat.format("Value for propertyName [{0}] not found in ETCD", propertyName));
        } catch (BaseException e) {
            LOGGER.error(MessageFormat.format("Error in getting value from ETCD by propertyName [{0}]: [{1}]", propertyName, e.getLocalizedMessage()),
                    e);
        } catch (Exception e) {
            LOGGER.debug(MessageFormat.format("CDI is not initialized, property [{0}] is unresolvable from ETCD: [{1}]", propertyName,
                    e.getLocalizedMessage()));
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        String urls = null;
        CDI<Object> cdi = CDI.current();
        Instance<DefaultEtcdConfigImpl> etcdConfigInstance = cdi.select(DefaultEtcdConfigImpl.class);
        urls = Arrays.toString(etcdConfigInstance.get().getUrl());
        cdi.destroy(etcdConfigInstance);
        return MessageFormat.format("{0} class on urls: [{1}]", DefaultEtcdConfigSource.class.getSimpleName(), urls);
    }
}
