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

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.module.etcd.config.DefaultEtcdConfigImpl;
import hu.icellmobilsoft.coffee.module.etcd.repository.EtcdRepository;
import hu.icellmobilsoft.coffee.module.etcd.service.ConfigEtcdService;
import hu.icellmobilsoft.coffee.module.etcd.service.EtcdService;
import hu.icellmobilsoft.coffee.module.etcd.util.EtcdClientBuilderUtil;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import io.etcd.jetcd.Client;

/**
 * ETCD client builder
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@ApplicationScoped
public class DefaultEtcdFactory {

    private static Logger log = Logger.getLogger(DefaultEtcdFactory.class);

    @Inject
    private DefaultEtcdConfigImpl defaultEtcdConfigImpl;

    /**
     * Producer for ETCD client
     * 
     * @return ETCD client
     */
    @ApplicationScoped
    @Produces
    public Client createEtcdClient() {
        Client etcdClient = null;
        try {
            etcdClient = EtcdClientBuilderUtil.getClientBuilder(defaultEtcdConfigImpl).build();
        } catch (BaseException e) {
            log.error("Problems trying to get the Etcd connection.", e);
        }
        return etcdClient;
    }

    /**
     * Close ETCD client
     *
     * @param etcdClient
     *            CDI parameter
     */
    public void closeEtcdClient(@Disposes Client etcdClient) {
        try {
            etcdClient.close();
            log.trace("etcdClient closed successfully");
        } catch (Exception e) {
            log.error("Error in closing etcd client: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * Producer for default EtcdRepository
     * 
     * @return EtcdRepository
     */
    @Produces
    @Dependent
    public EtcdRepository createEtcdRepository() {
        EtcdRepository etcdRepository = new EtcdRepository();
        Client client = CDI.current().select(Client.class).get();
        etcdRepository.init(client);
        return etcdRepository;
    }

    /**
     * Producer for default EtcdService
     * 
     * @return EtcdService
     */
    @Produces
    @Dependent
    public EtcdService createEtcdService() {
        EtcdService etcdService = new EtcdService();
        EtcdRepository etcdRepository = CDI.current().select(EtcdRepository.class).get();
        etcdService.init(etcdRepository);
        return etcdService;
    }

    /**
     * Disposes the managed EtcdService
     *
     * @param etcdService
     *            object to dispose
     */
    public void disposeEtcdService(@Disposes EtcdService etcdService) {
        if (etcdService != null) {
            CDI.current().destroy(etcdService.getEtcdRepository());
        }
    }

    /**
     * Producer for default ConfigEtcdService
     * 
     * @return ConfigEtcdService
     */
    @Produces
    @Dependent
    public ConfigEtcdService createConfigEtcdService() {
        ConfigEtcdService configEtcdService = new ConfigEtcdService();
        EtcdService etcdService = CDI.current().select(EtcdService.class).get();
        configEtcdService.init(etcdService);
        return configEtcdService;
    }

    /**
     * Disposes the managed ConfigEtcdService
     *
     * @param configEtcdService
     *            object to dispose
     */
    public void disposeConfigEtcdService(@Disposes ConfigEtcdService configEtcdService) {
        if (configEtcdService != null) {
            CDI.current().destroy(configEtcdService.getEtcdService());
        }
    }
}
