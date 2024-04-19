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
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.cdi.configsource.ConfigurableConfigSource;
import hu.icellmobilsoft.coffee.dto.exception.BONotFoundException;
import hu.icellmobilsoft.coffee.module.etcd.config.DefaultEtcdConfigImpl;
import hu.icellmobilsoft.coffee.module.etcd.config.EtcdConfig;
import hu.icellmobilsoft.coffee.module.etcd.repository.EtcdRepository;
import hu.icellmobilsoft.coffee.module.etcd.service.ConfigEtcdService;
import hu.icellmobilsoft.coffee.module.etcd.service.EtcdService;
import hu.icellmobilsoft.coffee.module.etcd.util.EtcdClientBuilderUtil;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import io.etcd.jetcd.Client;

/**
 * Microprofile-config implementation for configSource, use of cdi should be avoided
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
public class DefaultEtcdConfigSource extends ConfigurableConfigSource {

    private static Logger log = Logger.getLogger(DefaultEtcdConfigSource.class);

    private static ConfigEtcdService configEtcdService;

    /**
     * Default constructor, constructs a new object.
     */
    public DefaultEtcdConfigSource() {
        super();
    }

    @Override
    public int getOrdinal() {
        return 150;
    }

    @Override
    protected Map<String, String> getPropertiesIfEnabled() {
        try {
            return getConfigEtcdService().getAll();
        } catch (Exception e) {
            log.error(MessageFormat.format("Error in getting all values from ETCD: [{0}]", e.getLocalizedMessage()), e);
        }
        return Collections.emptyMap();
    }

    @Override
    public Set<String> getPropertyNames() {
        return getProperties().keySet();
    }

    private static ConfigEtcdService getConfigEtcdService() throws BaseException {
        if (configEtcdService == null) {
            synchronized (DefaultEtcdConfigSource.class) {
                if (configEtcdService != null) {
                    return configEtcdService;
                }
                configEtcdService = createConfigEtcdService();
            }
        }
        return configEtcdService;
    }

    private static ConfigEtcdService createConfigEtcdService() throws BaseException {
        EtcdConfig config = new DefaultEtcdConfigImpl();
        Client etcdClient = EtcdClientBuilderUtil.getClientBuilder(config).build();

        EtcdRepository etcdRepository = new EtcdRepository();
        etcdRepository.init(etcdClient);

        EtcdService etcdService = new EtcdService();
        etcdService.init(etcdRepository);

        ConfigEtcdService local = new ConfigEtcdService();
        local.init(etcdService);
        return local;
    }

    @Override
    public String getValueIfEnabled(String propertyName) {
        try {
            return readValue(propertyName).orElse(null);
        } catch (BaseException e) {
            log.error(MessageFormat.format("Error in getting value from ETCD by propertyName [{0}]: [{1}]", propertyName, e.getLocalizedMessage()),
                    e);
        } catch (Exception e) {
            log.debug(MessageFormat.format("CDI is not initialized, property [{0}] is unresolvable from ETCD: [{1}]", propertyName,
                    e.getLocalizedMessage()));
        }
        return null;
    }

    /**
     * Read value from ETCD
     *
     * @param propertyName
     *            key in ETCD
     * @return value Optional value
     * @throws BaseException
     *             connection or similar exception
     */
    protected Optional<String> readValue(String propertyName) throws BaseException {
        return readEtcdValue(propertyName);
    }

    /**
     * ETCD read flow
     *
     * @param propertyName
     *            key in ETCD
     * @return value Optional value
     * @throws BaseException
     *             connection or similar exception
     */
    public static Optional<String> readEtcdValue(String propertyName) throws BaseException {
        if (StringUtils.isBlank(propertyName)) {
            return Optional.empty();
        }
        try {
            String value = DefaultEtcdConfigSource.getConfigEtcdService().getValue(propertyName);
            return Optional.of(value);
        } catch (BONotFoundException e) {
            log.trace(e.getLocalizedMessage());
        }
        return Optional.empty();
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }
}
