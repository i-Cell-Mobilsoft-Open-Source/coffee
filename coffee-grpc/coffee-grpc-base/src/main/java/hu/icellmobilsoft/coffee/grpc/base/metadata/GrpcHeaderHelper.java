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
package hu.icellmobilsoft.coffee.grpc.base.metadata;

import java.text.MessageFormat;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.grpc.api.metadata.IGrpcHeader;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import io.grpc.Metadata;

/**
 * Grpc communication header helper
 * 
 * @author Imre Scheffer
 * @since 2.7.0
 */
public class GrpcHeaderHelper {

    /**
     * Default constructor, constructs a new object.
     */
    private GrpcHeaderHelper() {
        super();
    }

    /**
     * Get Locale parameter from Grpc request metadata
     * 
     * @param requestHeaders
     *            Grpc request metadata
     * @return Locale from request
     */
    public static Locale getRequestLocale(Metadata requestHeaders) {
        if (requestHeaders == null) {
            return null;
        }
        String languageString = requestHeaders.get(IGrpcHeader.HEADER_LANGUAGE);
        if (StringUtils.isNotBlank(languageString)) {
            try {
                return new Locale(languageString);
            } catch (Exception e) {
                String msg = MessageFormat.format("Failed to determine Locale setting from language header [{0}], value [{1}]: [{2}]",
                        IGrpcHeader.HEADER_LANGUAGE.originalName(), languageString, e.getLocalizedMessage());
                Logger.getLogger(GrpcHeaderHelper.class).debug(msg, e);
            }
        }
        return null;
    }
}
