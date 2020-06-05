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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jboss.logging.Logger;

import hu.icellmobilsoft.coffee.module.etcd.config.DefaultEtcdConfigImpl;
import io.etcd.jetcd.Client;

/**
 * ETCD client builder
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@ApplicationScoped
public class DefaultEtcdFactory {

    private static Logger LOGGER = hu.icellmobilsoft.coffee.cdi.logger.LogProducer.getStaticLogger(DefaultEtcdFactory.class);

    @Inject
    private DefaultEtcdConfigImpl defaultEtcdConfigImpl;

    /**
     * Producer for ETCD client
     */
    @ApplicationScoped
    @Produces
    public Client createEtcdClient() {
        Client etcdClient = null;
        try {
            etcdClient = Client.builder().endpoints(defaultEtcdConfigImpl.getUrl()).build();
        } catch (Exception e) {
            LOGGER.error("Problems trying to get the Etcd connection.", e);
        }
        return etcdClient;
    }

    /**
     * Close ETCD client
     *
     * @param etcdClient
     */
    public void closeEtcdClient(@Disposes Client etcdClient) {
        try {
            etcdClient.close();
            LOGGER.trace("etcdClient closed successfully");
        } catch (Exception e) {
            LOGGER.error("Error in closing etcd client: " + e.getLocalizedMessage(), e);
        }
    }
}
