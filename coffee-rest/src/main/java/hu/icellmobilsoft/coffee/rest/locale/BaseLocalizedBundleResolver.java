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

package hu.icellmobilsoft.coffee.rest.locale;

import java.io.Serializable;
import java.util.Locale;

import javax.inject.Inject;

import org.apache.deltaspike.core.api.message.LocaleResolver;
import org.apache.deltaspike.core.api.message.Message;
import org.apache.deltaspike.core.api.message.MessageContext;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;

/**
 * Szótár kezelésére szolgáló általános osztály
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
public abstract class BaseLocalizedBundleResolver {

    @Inject
    @ThisLogger
    private AppLogger log;

    @Inject
    private MessageContext messageContext;

    @Inject
    private LocaleResolver localeResolver;

    /**
     * for properties like this:<br>
     * <code>
     * hello=Hello, %s is %d
     * </code><br>
     * can use this syntax: <code><br>
     * messageTemplate("{hello}", "now", date) <code><br>
     * getting something like: "Hello, now is 2016-12-12 12:12:12"
     *
     * @see {@link http://deltaspike.apache.org/documentation/core.html#CreatingMessageInstances}
     * @param template
     *            key from properties file
     * @param arguments
     *            parameters in value of key
     * @return localized value by REST header
     */
    public String message(String template, Serializable... arguments) {
        Message message = messageContext.localeResolver(localeResolver).messageSource(getSources()).message();
        return message.template(template).argument(arguments).toString();
    }

    /**
     * for properties like this:<br>
     * <code>
     * hello=Hello, %s is %d
     * </code><br>
     * can use this syntax: <code><br>
     * messageTemplate("{hello}", "now", date) <code><br>
     * getting something like: "Hello, now is 2016-12-12 12:12:12"
     *
     * @see {@link http://deltaspike.apache.org/documentation/core.html#CreatingMessageInstances}
     * @param template
     *            key from properties file
     * @param arguments
     *            parameters in value of key
     * @return localized value by REST header
     */
    public String messageArray(String template, Serializable[] arguments) {
        Message message = messageContext.localeResolver(localeResolver).messageSource(getSources()).message();
        return message.template(template).argumentArray(arguments).toString();
    }

    /**
     * Message by enum value
     *
     * @param enumm
     * @param arguments
     */
    public String message(Enum<?> enumm, Serializable... arguments) {
        return message(enumm.getClass(), enumm.name(), arguments);
    }

    /**
     * Message by enum value
     *
     * @param enumm
     * @param arguments
     */
    public String messageArray(Enum<?> enumm, Serializable[] arguments) {
        return messageArray(enumm.getClass(), enumm.name(), arguments);
    }

    /**
     * message by class, key and arguments
     *
     * @param clazz
     * @param key
     * @param arguments
     */
    public <T> String message(Class<T> clazz, String key, Serializable... arguments) {
        if (clazz == null) {
            return message("{" + key + "}", arguments);
        }
        return message("{" + clazz.getName() + "." + key + "}", arguments);
    }

    /**
     * message array by class, key and arguments
     *
     * @param clazz
     * @param key
     * @param arguments
     */
    public <T> String messageArray(Class<T> clazz, String key, Serializable[] arguments) {
        if (clazz == null) {
            return messageArray("{" + key + "}", arguments);
        }
        return messageArray("{" + clazz.getName() + "." + key + "}", arguments);
    }

    /**
     * <p>messageByLanguage.</p>
     */
    public String messageByLanguage(String language, String template, Serializable... arguments) {
        LocaleResolver customLocaleResolver = new LocaleResolver() {
            private static final long serialVersionUID = 1L;

            @Override
            public Locale getLocale() {
                return new Locale(language);
            }
        };
        Message message = messageContext.localeResolver(customLocaleResolver).messageSource(getSources()).message();
        return message.template(template).argument(arguments).toString();
    }

    /**
     * <p>getSources.</p>
     */
    protected abstract String[] getSources();

}
