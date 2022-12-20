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
package hu.icellmobilsoft.coffee.module.mp.restclient.util;

import jakarta.enterprise.inject.Vetoed;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;

/**
 * Microprofile REST Client hasznalatnal hasznot util gyujto
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Vetoed
public class MPRestClientUtil {

    /**
     * Altalanos REST Client exception konverter framework szinture
     *
     * @param e
     *            exception
     * @return BaseException leszarmazott
     */
    public static BaseException toBaseException(Exception e) {
        if (e instanceof WebApplicationException) {
            return toBaseException((WebApplicationException) e);
        } else if (e instanceof ProcessingException) {
            return toBaseException((ProcessingException) e);
        } else {
            return new TechnicalException(CoffeeFaultType.OPERATION_FAILED, "REST client unhandled exception: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * A lekezelt {@link ResponseExceptionMapper} altal nyujtott hibak ebben vannak nativan csomgolva
     *
     * @param e
     *            WebApplicationException
     * @return BaseException leszarmazott
     * @see hu.icellmobilsoft.coffee.module.mp.restclient.provider.DefaultBaseExceptionResponseExceptionMapper
     */
    public static BaseException toBaseException(WebApplicationException e) {
        if (e.getCause() instanceof BaseException) {
            return (BaseException) e.getCause();
        } else {
            return new TechnicalException(CoffeeFaultType.OPERATION_FAILED, "REST client handled exception: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * A REST cliens hasznala alatt elofordulo hibak, peldaul {@link RestClientBuilder#readTimeout(long, java.util.concurrent.TimeUnit)} eltelte soran
     * dobodik
     *
     * @param e
     *            ProcessingException
     * @return TechnicalException
     */
    public static TechnicalException toBaseException(ProcessingException e) {
        return new TechnicalException(CoffeeFaultType.OPERATION_FAILED, e.getLocalizedMessage(), e);
    }
}
