/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2022 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.deltaspike.test.message.localeresolver;

import java.util.Locale;

import jakarta.inject.Inject;

import org.apache.deltaspike.core.api.message.LocaleResolver;
import org.apache.deltaspike.core.impl.message.DefaultLocaleResolver;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author Imre Scheffer
 * @since 2.0.0
 *
 */
@EnableWeld
@Tag("weld")
@ExtendWith(WeldJunit5Extension.class)
@DisplayName("DefaultLocaleResolver tests")
class DefaultLocaleResolverTest {

    @Inject
    LocaleResolver localeResolver;

    @WeldSetup
    public WeldInitiator weld = WeldInitiator.from(WeldInitiator.createWeld()
            // add beans
            .addBeanClasses(DefaultLocaleResolver.class))
            // build
            .build();

    @Test
    @DisplayName("default locale resolver test")
    void defaultLocale() {
        Assertions.assertEquals(Locale.getDefault(), localeResolver.getLocale());
    }
}
