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

import com.google.rpc.LocalizedMessage;

import hu.icellmobilsoft.coffee.se.api.exception.BaseException;

/**
 * Interface for translating exceptions to Grpc LocalizedMessage.
 *
 * @author Imre Scheffer
 * @since 2.7.0
 */
public interface IGrpcExceptionTranslator {

    /**
     * Creates a {@link LocalizedMessage} from a given {@link BaseException}.
     * 
     * @param locale
     *            The locale used following the specification defined at http://www.rfc-editor.org/rfc/bcp/bcp47.txt. Examples are: "en-US", "fr-CH",
     *            "es-MX".
     * @param e
     *            {@link BaseException} to localize
     * @return Translated Grpc {@link LocalizedMessage} from exception
     */
    default LocalizedMessage.Builder toLocalizedMessage(Locale locale, BaseException e) {
        if (e == null) {
            return LocalizedMessage.newBuilder();
        }
        return toLocalizedMessage(locale, e.getFaultTypeEnum());
    }

    /**
     * Creates a {@link LocalizedMessage} from a given Enum.
     * 
     * @param locale
     *            The locale used following the specification defined at http://www.rfc-editor.org/rfc/bcp/bcp47.txt. Examples are: "en-US", "fr-CH",
     *            "es-MX"
     * @param faultType
     *            Enum to localize
     * @param messageArguments
     *            Message arguments to fill faulType template with values
     * @return Translated Grpc {@link LocalizedMessage} from enum
     */
    LocalizedMessage.Builder toLocalizedMessage(Locale locale, Enum<?> faultType, Serializable... messageArguments);
}
