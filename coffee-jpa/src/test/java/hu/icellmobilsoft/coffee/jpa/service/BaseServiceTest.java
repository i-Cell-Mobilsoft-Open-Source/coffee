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
package hu.icellmobilsoft.coffee.jpa.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;

/**
 * @author zsolt.tomai
 */
@DisplayName("Testing BaseService")
public class BaseServiceTest {

    @Test
    @DisplayName("Testing repositoryFailed without params")
    public void testRepositoryFailedWithoutParams() {
        // given
        BaseService baseService = new BaseService();
        String methodInfo = baseService.getCalledMethodWithParamsBase("testMethod").concat(")");
        Exception e = new Exception("test exception message with {brace} bracket");

        // when
        TechnicalException technicalException = baseService.repositoryFailed(e, methodInfo);

        // then
        Assertions.assertEquals(CoffeeFaultType.REPOSITORY_FAILED, technicalException.getFaultTypeEnum());
        Assertions.assertEquals("Error occurred in  BaseService.testMethod() : [test exception message with {brace} bracket]",
                technicalException.getMessage());
    }

    @Test
    @DisplayName("Testing repositoryFailed with param")
    public void testRepositoryFailedWithParams() {
        // given
        BaseService baseService = new BaseService();
        String methodInfo = baseService.getCalledMethodWithParamsBase("testMethod", "param1").concat(")");
        Exception e = new Exception("test exception message with {brace} bracket");

        // when
        TechnicalException technicalException = baseService.repositoryFailed(e, methodInfo, "param1");

        // then
        Assertions.assertEquals(CoffeeFaultType.REPOSITORY_FAILED, technicalException.getFaultTypeEnum());
        Assertions.assertEquals("Error occurred in  BaseService.testMethod(param1: [param1]) : [test exception message with {brace} bracket]",
                technicalException.getMessage());
    }
}
