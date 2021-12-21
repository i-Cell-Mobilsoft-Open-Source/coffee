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
package hu.icellmobilsoft.coffee.module.localization;

import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.commons.lang3.ArrayUtils;

import hu.icellmobilsoft.coffee.cdi.config.IConfigKey;
import hu.icellmobilsoft.coffee.configuration.ApplicationConfiguration;

/**
 * Nyelvesítés kezelésére szolgáló osztály.<br>
 * A szótár fájlokat a {@link IConfigKey#RESOURCE_BUNDLES} konfiguráció segítségével lehet beállítani.
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Dependent
public class LocalizedMessage extends BaseLocalizedBundleResolver {

    @Inject
    private ApplicationConfiguration applicationConfiguration;

    /** Constant <code>SOURCE</code> */
    public static final String[] SOURCE = { "i18n.messages" };

    /** Constant <code>BUNDLED_SOURCES</code> */
    public static final String[] BUNDLED_SOURCES = { "i18n.common-messages" };

    /** {@inheritDoc} */
    @Override
    protected String[] getSources() {
        Optional<String[]> optional = applicationConfiguration.getOptionalValue(IConfigKey.RESOURCE_BUNDLES, String[].class);
        String[] configuredSources = optional.orElse(SOURCE);
        return ArrayUtils.addAll(configuredSources, BUNDLED_SOURCES);
    }
}
