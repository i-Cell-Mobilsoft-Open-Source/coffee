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
package hu.icellmobilsoft.coffee.module.etcd.config;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * ETCD configuration values from microprofile-config
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Dependent
public class DefaultEtcdConfigImpl implements EtcdConfig {

    @SuppressWarnings("cdi-ambiguous-dependency")
    @Inject
    @ConfigProperty(name = "etcd.default.url", defaultValue = "http://localhost:2379")
    private Provider<String> url;

    /** {@inheritDoc} */
    @Override
    public String[] getUrl() {
        String urlString = url.get();
        return StringUtils.split(urlString, ",");
    }
}
