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
package hu.icellmobilsoft.coffee.quarkus.extension.module.mp.restclient.test;

import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import hu.icellmobilsoft.coffee.module.mp.restclient.exception.FaultTypeClasses;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Test that FaultTypeClasses interface injectable test
 *
 * @since 2.6.0
 * @author speter555
 */
@QuarkusTest
class FaultTypeParserTest {

    // NOTE: Come from buildStep as a SyntheticBean
    @Inject
    FaultTypeClasses faultTypeClasses;

    @Inject
    Instance<FaultTypeClasses> faultTypeClassesInstance;

    @Test
    void canInjectFaulTypeClasses() {
        var localFaultTypeClasses = faultTypeClassesInstance.get();
        var localFaultTypeClassesWithCDI = CDI.current().select(FaultTypeClasses.class).get();
        Assertions.assertNotNull(localFaultTypeClassesWithCDI);
        Assertions.assertNotNull(localFaultTypeClassesWithCDI.getFaultTypeClasses());
        Assertions.assertFalse(localFaultTypeClassesWithCDI.getFaultTypeClasses().isEmpty());
        Assertions.assertNotNull(localFaultTypeClasses);
        Assertions.assertNotNull(localFaultTypeClasses.getFaultTypeClasses());
        Assertions.assertFalse(localFaultTypeClasses.getFaultTypeClasses().isEmpty());
        Assertions.assertNotNull(faultTypeClasses);
        Assertions.assertNotNull(faultTypeClasses.getFaultTypeClasses());
        Assertions.assertFalse(faultTypeClasses.getFaultTypeClasses().isEmpty());
    }
}
