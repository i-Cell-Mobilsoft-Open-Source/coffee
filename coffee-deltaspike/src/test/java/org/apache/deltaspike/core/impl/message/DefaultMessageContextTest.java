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
package org.apache.deltaspike.core.impl.message;

import java.io.Serializable;
import java.util.Locale;

import jakarta.inject.Inject;

import org.apache.deltaspike.core.api.message.LocaleResolver;
import org.apache.deltaspike.core.api.message.Message;
import org.apache.deltaspike.core.api.message.MessageContext;
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
@DisplayName("DefaultMessageContext tests")
class DefaultMessageContextTest {

    public static final String[] SOURCE = { "i18n.messages" };

    @Inject
    MessageContext messageContext;

    @Inject
    LocaleResolver localeResolver;

    @WeldSetup
    public WeldInitiator weld = WeldInitiator.from(WeldInitiator.createWeld()
            // add beans
            .addBeanClasses(DefaultMessageContext.class, DefaultLocaleResolver.class, DefaultMessageResolver.class, DefaultMessageInterpolator.class))
            // build
            .build();

    @Test
    @DisplayName("default locale test")
    void defaultLocale() {
        Assertions.assertEquals(Locale.getDefault(), messageContext.getLocale());
    }

    @Test
    @DisplayName("default without argument")
    void defaultWithoutArgument() {
        Message message = messageContext.localeResolver(localeResolver).messageSource(SOURCE).message();
        String translated = message.template("{first}")// .argument(arguments)
                .toString();
        Assertions.assertEquals("first default", translated);
    }

    @Test
    @DisplayName("default with argument")
    void defaultWithArgument() {
        Message message = messageContext.localeResolver(localeResolver).messageSource(SOURCE).message();
        String translated = message.template("{first.argument}").argument("blabla").toString();
        Assertions.assertEquals("first default with argument blabla", translated);
    }

    @Test
    @DisplayName("hu with argument")
    void huWithArgument() {
        String translated = messageByLanguage("hu", "{first.argument}", "blabla");
        Assertions.assertEquals("első argumentummal blabla", translated);
    }

    private String messageByLanguage(String language, String template, Serializable... arguments) {
        LocaleResolver customLocaleResolver = new LocaleResolver() {
            private static final long serialVersionUID = 1L;

            @Override
            public Locale getLocale() {
                return new Locale(language);
            }
        };
        Message message = messageContext.localeResolver(customLocaleResolver).messageSource(SOURCE).message();
        return message.template(template).argument(arguments).toString();
    }
}
