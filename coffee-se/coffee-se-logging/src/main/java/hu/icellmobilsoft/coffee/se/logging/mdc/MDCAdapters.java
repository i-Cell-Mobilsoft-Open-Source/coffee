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
package hu.icellmobilsoft.coffee.se.logging.mdc;

import java.util.ServiceLoader;

import hu.icellmobilsoft.coffee.se.logging.DefaultLogger;
import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * Utility class for finding available MDC adapters
 * 
 * @author mark.petrenyi
 * @since 1.1.0
 */
public class MDCAdapters {

    private static final Logger LOGGER = DefaultLogger.getLogger(MDCAdapters.class);

    /**
     * Finds available MDC Adapter. First tried via service loader, if not found one of the deafult adapters is returned tried in the following order:
     * <ul>
     * <li>{@link JbossMDCAdapter} - for using {@code org.jboss.logging.MDC}</li>
     * <li>{@link Slf4jMDCAdapter} - for using {@code org.slf4j.MDC}</li>
     * <li>{@link CoffeeMDCAdapter} - fallback MDC, uses a {@link ThreadLocal}</li>
     * </ul>
     * 
     * @return An instance of {@link MDCAdapter}
     */
    public static MDCAdapter findAdapter() {
        // Try service loader mechanism
        ServiceLoader<MDCAdapterProvider> mdcAdapterProviderLoader = ServiceLoader.load(MDCAdapterProvider.class);
        for (MDCAdapterProvider mdcAdapterProvider : mdcAdapterProviderLoader) {
            String providerName = mdcAdapterProvider.getClass().getName();
            try {
                LOGGER.trace("Loading MDCAdapter from MDCAdapterProvider:[{0}]", providerName);
                return mdcAdapterProvider.getAdapter();
            } catch (Exception e) {
                LOGGER.trace("Could not load MDCAdapter from MDCAdapterProvider:[{0}], [{1}]", e.getLocalizedMessage(), providerName);
            }
        }

        // Try jboss
        try {
            LOGGER.trace("Loading jboss MDC...");
            return new JbossMDCAdapter();
        } catch (Exception e) {
            LOGGER.trace("Could not load jboss MDC:[{0}]", e.getLocalizedMessage());
        }

        // Try slf4j
        try {
            LOGGER.trace("Loading slf4j MDC...");
            return new Slf4jMDCAdapter();
        } catch (Exception e) {
            LOGGER.trace("Could not load slf4j MDC:[{0}]", e.getLocalizedMessage());
        }

        // Create default
        LOGGER.warn("Could not find any MDC implementation, falling back to CoffeeMDCAdapter!");
        return new CoffeeMDCAdapter();
    }

}
