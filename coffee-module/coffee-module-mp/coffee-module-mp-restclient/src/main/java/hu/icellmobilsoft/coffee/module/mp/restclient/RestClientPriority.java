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
package hu.icellmobilsoft.coffee.module.mp.restclient;

import javax.ws.rs.Priorities;

/**
 * HTTP Rest Clienseken hasznalt fiterek priority gyujtoje
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
public interface RestClientPriority {

    /**
     * HTTP REST Client request loggolas
     */
    static final int REQUEST_LOG = Priorities.HEADER_DECORATOR + 100;

    /**
     * HTTP REST Client setting - sessionId, user, stb beallitasok
     */
    static final int REQUEST_SETTING = Priorities.AUTHENTICATION - 100;

    /**
     * HTTP REST Client response loggolas. <br>
     * A priority mindenkeppen a io.smallrye.restclient.ExceptionMapping bekotesnel magasabb kell hogy legyen, aminek jelenleg 1 az erteke
     */
    static final int RESPONSE_LOG = Priorities.AUTHENTICATION;

    /**
     * BaseException-re fordito HTTP ResponseExceptionMapper
     */
    static final int EXCEPTION_BASE = Priorities.USER;
}
