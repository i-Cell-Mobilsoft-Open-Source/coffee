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
package hu.icellmobilsoft.coffee.grpc.server.log;

import hu.icellmobilsoft.coffee.dto.common.LogConstants;
import hu.icellmobilsoft.coffee.se.logging.mdc.MDC;
import hu.icellmobilsoft.coffee.tool.utils.string.RandomUtil;
import io.grpc.Context;
import io.grpc.Context.Key;

/**
 * Grpc logging support
 * 
 * @author Imre Scheffer
 * @since 2.7.0
 */
public class GrpcLogging {

    /**
     * Processing session ID GRPC representation into Grpc context.
     * 
     * @see LogConstants#LOG_SESSION_ID
     */
    public static final Key<String> CONTEXT_KEY_SESSIONID = Context.key(LogConstants.LOG_SESSION_ID);

    /**
     * Default constructor, constructs a new object.
     */
    private GrpcLogging() {
        super();
    }

    /**
     * Session ID from current Grpc context
     * 
     * @return get "sid" value from Grpc context. If not exist then generate one
     */
    public static String getContextSessionId() {
        Object value = CONTEXT_KEY_SESSIONID.get();
        return value == null ? RandomUtil.generateId() : value.toString();
    }

    /**
     * Setting MDC with Grpc needed parameters
     * 
     * @param extSessionId
     *            "sid" in logs. It usually comes from a request
     */
    public static void handleMdc(String extSessionId) {
        MDC.put(LogConstants.LOG_SESSION_ID, extSessionId);
    }

    /**
     * Setting MDC with Grpc needed parameters
     */
    public static void handleMdc() {
        handleMdc(getContextSessionId());
    }
}
