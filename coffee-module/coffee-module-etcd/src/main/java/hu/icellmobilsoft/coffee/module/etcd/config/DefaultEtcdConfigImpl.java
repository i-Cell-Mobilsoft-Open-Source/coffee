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

import java.util.Optional;

import jakarta.enterprise.context.Dependent;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;

import hu.icellmobilsoft.coffee.tool.utils.config.ConfigUtil;

/**
 * ETCD configuration values from microprofile-config
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Dependent
public class DefaultEtcdConfigImpl implements EtcdConfig {

    /**
     * Configurationa key for ETCD URL
     */
    public static final String URL_KEY = "coffee.etcd.default.url";

    /** {@inheritDoc} */
    @Override
    public String[] getUrl() {
        String url = "";
        Optional<String> optUrl = ConfigUtil.getInstance().defaultConfig().getOptionalValue(URL_KEY, String.class);
        if (optUrl.isEmpty()) {
            Config config = ConfigProviderResolver.instance()
                    .getBuilder()
                    .forClassLoader(DefaultEtcdConfigImpl.class.getClassLoader())
                    .addDefaultSources()
                    .build();
            url = config.getOptionalValue(URL_KEY, String.class).orElse("http://localhost:2379");
        } else {
            url = optUrl.get();
        }
        return StringUtils.split(url, ",");
    }

}
