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
package hu.icellmobilsoft.coffee.rest.exception;

import jakarta.xml.bind.JAXBException;

import hu.icellmobilsoft.coffee.dto.common.commonservice.BaseExceptionResultType;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;

/**
 * Exception translator for exception throwing
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
public interface IExceptionMessageTranslator {

    /** Constant <code>HTTP_STATUS_I_AM_A_TEAPOT=418</code> */
    final int HTTP_STATUS_I_AM_A_TEAPOT = 418;

    /**
     * Fill Coffee DTO with data from exception
     *
     * @param dto
     *            Coffee DTO exception type to fill
     * @param e
     *            throwed BaseException
     */
    void addCommonInfo(BaseExceptionResultType dto, BaseException e);

    /**
     * Fill Coffee DTO with data from exception
     *
     * @param dto
     *            Coffee DTO exception type to fill
     * @param e
     *            throwed Exception
     * @param faultType
     *            to mark data with error code
     */
    void addCommonInfo(BaseExceptionResultType dto, Exception e, Enum<?> faultType);

    /**
     * Get linked exception message
     *
     * @param e
     *            exception
     * @return e.getLinkedException().getLocalizedMessage()
     */
    default String getLinkedExceptionLocalizedMessage(JAXBException e) {
        if (e == null) {
            return null;
        }
        Throwable t = e.getLinkedException();
        if (t != null) {
            return t.getLocalizedMessage();
        }
        return e.getLocalizedMessage();
    }

    /**
     * Error code to localized message converter.
     *
     * @param faultType
     *            key for localized resource bundle
     * @return Localized message
     */
    String getLocalizedMessage(Enum<?> faultType);
}
