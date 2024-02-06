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
package hu.icellmobilsoft.coffee.module.mp.restclient.provider;

import java.util.concurrent.TimeUnit;

import jakarta.enterprise.inject.spi.CDI;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.spi.RestClientBuilderListener;

/**
 * A <a href=
 * "https://download.eclipse.org/microprofile/microprofile-rest-client-1.2.1/microprofile-rest-client-1.2.1.html#_provider_declaration">Microprofile
 * Rest Client</a> szerinti REST kliens alap provider aktivalasara szolgalo osztaly. Ezt az osztalyt meg a
 * <code>META-INF/services/org.eclipse.microprofile.rest.client.spi.RestClientBuilderListener</code> fajlon keresztul aktivalni kell <br>
 * <br>
 * Tartalmazza a request/response loggolasat + alap ceges szintu http setting beallitasokat
 *
 * @author imre.scheffer
 * @see DefaultLoggerClientRequestFilter
 * @see DefaultLoggerClientResponseFilter
 * @see DefaultSettingClientRequestFilter
 * @since 1.0.0
 */
public class DefaultRestClientBuilderListener implements RestClientBuilderListener {

    /**
     * Default constructor, constructs a new object.
     */
    public DefaultRestClientBuilderListener() {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public void onNewBuilder(RestClientBuilder builder) {
        CDI<Object> cdi = CDI.current();
        // log
        // A filtereket CDI-on keresztül regisztráljuk, hogy feloldhatoak legyenek az Inject-ek
        builder.register(cdi.select(DefaultLoggerClientRequestFilter.class).get());
        builder.register(cdi.select(DefaultLoggerClientResponseFilter.class).get());
        // settings
        builder.register(cdi.select(DefaultSettingClientRequestFilter.class).get());
        builder.register(JsonbMPRestClientSettings.class);
        // exception
        builder.register(DefaultBaseExceptionResponseExceptionMapper.class);
        // timeout
        builder.connectTimeout(5, TimeUnit.SECONDS);
        builder.readTimeout(1, TimeUnit.MINUTES);
    }
}
