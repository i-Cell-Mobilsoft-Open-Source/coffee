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

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.xml.bind.JAXBException;

import hu.icellmobilsoft.coffee.dto.common.commonservice.BaseExceptionResultType;
import hu.icellmobilsoft.coffee.dto.common.commonservice.FunctionCodeType;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.RestClientResponseException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.module.localization.LocalizedMessage;
import hu.icellmobilsoft.coffee.rest.cdi.BaseApplicationContainer;
import hu.icellmobilsoft.coffee.rest.projectstage.ProjectStage;
import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * Default exception translator implementation for exception throwing
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Dependent
public class DefaultExceptionMessageTranslator implements IExceptionMessageTranslator {

    @Inject
    private BaseApplicationContainer baseApplicationContainer;

    /** Constant <code>HTTP_STATUS_I_AM_A_TEAPOT=418</code> */
    public static final int HTTP_STATUS_I_AM_A_TEAPOT = 418;

    @Inject
    private LocalizedMessage localizedMessage;

    @Inject
    private ProjectStage projectStage;

    /** {@inheritDoc} */
    @Override
    public void addCommonInfo(BaseExceptionResultType dto, BaseException e) {
        addCommonInfo(dto, e, e.getFaultTypeEnum());
    }

    /** {@inheritDoc} */
    @Override
    public void addCommonInfo(BaseExceptionResultType dto, Exception e, Enum<?> faultType) {
        boolean putExceptionToResponse = !projectStage.isProductionStage();
        if (putExceptionToResponse) {
            if (e instanceof JAXBException) {
                dto.setException(getLinkedExceptionLocalizedMessage((JAXBException) e));
            } else {
                dto.setException(e.getLocalizedMessage());
            }

            if (e.getCause() != null) {
                var causedBy = new BaseExceptionResultType();
                addCausedByInfo(causedBy, e.getCause(), faultType);
                dto.setCausedBy(causedBy);
            }

            dto.setClassName(e.getClass().getName());
        }
        dto.setFaultType(faultType.name());
        dto.setFuncCode(FunctionCodeType.ERROR);

        // nyelvesitett valasz kell a faultype szerint
        dto.setMessage(getLocalizedMessage(faultType));

        if (e instanceof RestClientResponseException) {
            var restClientResponseException = (RestClientResponseException) e;
            dto.setService(restClientResponseException.getService());
        } else {
            dto.setService(baseApplicationContainer.getCoffeeAppName());
        }
    }

    private void addCausedByInfo(BaseExceptionResultType dto, Throwable t, Enum<?> faultType) {
        dto.setClassName(t.getClass().getName());
        dto.setMessage(t.getLocalizedMessage());
        if (t instanceof BaseException) {
            dto.setFaultType(((BaseException) t).getFaultTypeEnum().name());
        } else {
            dto.setFaultType(faultType.name());
        }
        dto.setFuncCode(FunctionCodeType.ERROR);

        if (t.getCause() != null) {
            var causedBy = new BaseExceptionResultType();
            addCausedByInfo(causedBy, t.getCause(), faultType);
            dto.setCausedBy(causedBy);
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getLocalizedMessage(Enum<?> faultType) {
        if (faultType == null) {
            Logger.getLogger(DefaultExceptionMessageTranslator.class)
                    .warn("FaultType is null, proceeding with faultType: [" + CoffeeFaultType.OPERATION_FAILED + "]");
            return localizedMessage.message(CoffeeFaultType.OPERATION_FAILED);
        }
        return localizedMessage.message(faultType);
    }

}
