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
package hu.icellmobilsoft.coffee.tool.utils.config;

import javax.enterprise.inject.Vetoed;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;

/**
 * ConfigUtil
 * 
 * @author czenczl
 * @since 1.2.0
 */
@Vetoed
public class ConfigUtil {

    /**
     * Get the default configuration sources
     * <ol>
     * <li>System properties</li>
     * <li>Environment properties</li>
     * <li>/META-INF/microprofile-config.properties</li>
     * </ol>
     * 
     * @return default microprofile config without other configsources
     */
    public static Config defaultConfig() {
        // Lefixaljuk arra a classloader-re ahol kezeljunk a kodunkat, ezzel biztositjuk hogyha netan az alkalmazas szerver mas classloaderrel
        // inicializal mindig megtalalja a sajat 'microprofile-config.properties' fajlunkat
        return ConfigProviderResolver.instance().getBuilder().forClassLoader(ConfigUtil.class.getClassLoader()).addDefaultSources().build();
    }
}
