/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2024 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.grpc.server.mapper;

import java.io.Serializable;
import java.util.Locale;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.google.rpc.LocalizedMessage;
import com.google.rpc.LocalizedMessage.Builder;

/**
 * Default implementation for translating exceptions to status.
 *
 * @author Imre Scheffer
 * @since 2.7.0
 */
@ApplicationScoped
public class DefaultGrpcExceptionTranslator implements IGrpcExceptionTranslator {

    /**
     * Default Locale for Grpc message translation
     */
    public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

    @Inject
    private hu.icellmobilsoft.coffee.module.localization.LocalizedMessage localizedMessage;

    /**
     * Default constructor, constructs a new object.
     */
    public DefaultGrpcExceptionTranslator() {
        super();
    }

    @Override
    public Builder toLocalizedMessage(Locale locale, Enum<?> faultType, Serializable... messageArguments) {
        LocalizedMessage.Builder lmBuilder = LocalizedMessage.newBuilder();
        if (faultType != null) {
            Locale returnLocale = locale == null ? DEFAULT_LOCALE : locale;
            // localizedMessage.message(faultType, messageArguments) Currently not suitable because
            // it reads the language key from the REST header and there is no request scope here.
            String translatedMessage = localizedMessage.messageByLanguage(returnLocale.getLanguage(),
                    "{" + faultType.getClass().getName() + "." + faultType.name() + "}", messageArguments);
            lmBuilder.setLocale(returnLocale.toString()).setMessage(translatedMessage);
        }
        return lmBuilder;
    }
}
