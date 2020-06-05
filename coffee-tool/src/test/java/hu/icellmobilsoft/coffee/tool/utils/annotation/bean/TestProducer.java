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
package hu.icellmobilsoft.coffee.tool.utils.annotation.bean;

import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import hu.icellmobilsoft.coffee.tool.utils.annotation.AnnotationUtil;

/**
 * Test producer for AnnotationUtilTest
 *
 * @author mark.petrenyi
 */
@Dependent
public class TestProducer {

    @Dependent
    @Produces
    @TestQualifier(testString = "")
    public String produceTestString(InjectionPoint injectionPoint) {
        Optional<TestQualifier> annotation = AnnotationUtil.getAnnotation(injectionPoint, TestQualifier.class);
        return annotation.map(TestQualifier::testString).orElse(null);
    }
}
