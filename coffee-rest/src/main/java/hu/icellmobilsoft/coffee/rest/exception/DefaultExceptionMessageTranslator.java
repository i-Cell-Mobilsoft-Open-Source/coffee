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

import javax.inject.Inject;
import javax.xml.bind.JAXBException;

import org.apache.deltaspike.core.api.projectstage.ProjectStage;

import hu.icellmobilsoft.coffee.cdi.logger.LogProducer;
import hu.icellmobilsoft.coffee.dto.common.commonservice.BaseExceptionResultType;
import hu.icellmobilsoft.coffee.dto.common.commonservice.FunctionCodeType;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.rest.locale.LocalizedMessage;

/**
 * Default exception translator implementation for exception throwing
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
public class DefaultExceptionMessageTranslator implements IExceptionMessageTranslator {

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
        boolean putExceptionToResponse = !ProjectStage.Production.equals(projectStage);
        if (putExceptionToResponse) {
            if (e instanceof JAXBException) {
                dto.setMessage(getLinkedExceptionLocalizedMessage((JAXBException) e));
            } else {
                dto.setMessage(e.getLocalizedMessage());
            }
            dto.setClassName(e.getClass().getName());
            dto.setException(e.getLocalizedMessage());
        }
        dto.setFuncCode(FunctionCodeType.ERROR);
        dto.setFaultType(faultType.name());
        // nyelvesitett valasz kell a faultype szerint
        dto.setMessage(getLocalizedMessage(faultType));
    }

    /** {@inheritDoc} */
    @Override
    public String getLocalizedMessage(Enum<?> faultType) {
        if (faultType == null) {
            LogProducer.getStaticLogger(DefaultExceptionMessageTranslator.class)
                    .warn("FaultType is null, proceeding with faultType: [" + CoffeeFaultType.OPERATION_FAILED + "]");
            return localizedMessage.message(CoffeeFaultType.OPERATION_FAILED);
        }
        return localizedMessage.message(faultType);
    }

}
