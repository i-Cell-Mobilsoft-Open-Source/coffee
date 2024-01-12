/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2023 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.rest.jsonb;

import hu.icellmobilsoft.coffee.rest.projectstage.ProjectStageProducer;
import jakarta.json.bind.Jsonb;

import jakarta.json.bind.JsonbBuilder;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.ExplicitParamInjection;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import hu.icellmobilsoft.coffee.dto.common.commonservice.BaseResponse;
import hu.icellmobilsoft.coffee.dto.common.commonservice.ContextType;
import hu.icellmobilsoft.coffee.dto.common.commonservice.FunctionCodeType;
import hu.icellmobilsoft.coffee.rest.provider.util.JsonbUtil;
import hu.icellmobilsoft.coffee.tool.utils.date.DateUtil;
import hu.icellmobilsoft.coffee.tool.utils.string.RandomUtil;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Customer Jsonb handling
 * @author speter555
 * @since 2.5.0
 */
@EnableWeld
@Tag("weld")
@ExtendWith(WeldJunit5Extension.class)
@DisplayName("CustomJsonb injection tests")
@ExplicitParamInjection
class CustomJsonbTest {

    @WeldSetup
    public WeldInitiator weld = WeldInitiator.from(WeldInitiator.createWeld()
                    // add beans
                    .addBeanClasses(ProjectStageProducer.class))
            // build
            .build();

    @BeforeEach
    public void config() {
        System.setProperty("jsonb.null-values","false");
        System.setProperty("jsonb.property-visibility-strategy","org.eclipse.yasson.FieldAccessStrategy");
    }
    @Test
    @DisplayName("injected Jsonb instance test")
    void getJsonB() {
        Jsonb jsonb = JsonbBuilder.newBuilder().build();
        Assertions.assertNotNull(jsonb);
        BaseResponse object = new BaseResponse().withFuncCode(FunctionCodeType.OK).withContext(new ContextType().withTimestamp(DateUtil.nowUTCTruncatedToMillis()).withRequestId(RandomUtil.generateId()));
        String json = jsonb.toJson(object);
        Assertions.assertFalse(json.contains("set"));
    }
}
