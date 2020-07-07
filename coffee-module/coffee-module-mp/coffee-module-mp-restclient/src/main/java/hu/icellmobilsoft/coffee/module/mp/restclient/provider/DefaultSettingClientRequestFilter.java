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

import java.io.IOException;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

import hu.icellmobilsoft.coffee.dto.common.LogConstants;
import hu.icellmobilsoft.coffee.module.mp.restclient.RestClientPriority;
import hu.icellmobilsoft.coffee.se.logging.mdc.MDC;

/**
 * Rest Client default request setting filter. Serviceken keresztul osszekotheto sessionId valtozo beallitasa hogy a loggolas es az authentikacio
 * alapjai tudjanak mukodni
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Priority(value = RestClientPriority.REQUEST_SETTING)
@Dependent
public class DefaultSettingClientRequestFilter implements ClientRequestFilter {

    /** {@inheritDoc} */
    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        if (MDC.get(LogConstants.LOG_SESSION_ID) != null) {
            requestContext.getHeaders().add(LogConstants.LOG_SESSION_ID, MDC.get(LogConstants.LOG_SESSION_ID).toString());
        }
    }
}
