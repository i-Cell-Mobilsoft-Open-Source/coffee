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

import jakarta.ws.rs.Priorities;

/**
 * A collection of priorities for filters used in HTTP Rest Clients
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
public interface RestClientPriority {

    /**
     * HTTP REST Client request logging
     */
    static final int REQUEST_LOG = Priorities.HEADER_DECORATOR + 100;

    /**
     * HTTP REST Client setting - sessionId, user, e.g. settings
     */
    static final int REQUEST_SETTING = Priorities.AUTHENTICATION - 100;

    /**
     * HTTP REST Client response logging. <br>
     * The priority value must be higher than "1" for the io.smallrye.restclient.ExceptionMapping binding
     */
    static final int RESPONSE_LOG = Priorities.AUTHENTICATION;

    /**
     * HTTP ResponseExceptionMapper that translates to BaseException
     */
    static final int EXCEPTION_BASE = Priorities.USER;
}
