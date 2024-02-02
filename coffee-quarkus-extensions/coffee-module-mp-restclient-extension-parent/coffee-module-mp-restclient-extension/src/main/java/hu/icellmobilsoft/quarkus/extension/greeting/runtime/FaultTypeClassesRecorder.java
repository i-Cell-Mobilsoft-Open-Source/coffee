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
package hu.icellmobilsoft.quarkus.extension.greeting.runtime;

import java.util.List;
import java.util.function.Function;

import hu.icellmobilsoft.coffee.module.mp.restclient.exception.FaultTypeClasses;
import io.quarkus.arc.SyntheticCreationalContext;
import io.quarkus.runtime.annotations.Recorder;

/**
 * Recorder of FaultTypeClasses
 *
 * @since 2.6.0
 * @author speter555
 */
@Recorder
public class FaultTypeClassesRecorder {

    /**
     * Function for SyntheticCreationalContext of FaultTypeClasses interface
     * 
     * @param faultTypeClasses
     *            list of enum classNames
     * @return function
     */
    public Function<SyntheticCreationalContext<FaultTypeClasses>, FaultTypeClasses> createFaultTypeClasses(
            List<Class<? extends Enum>> faultTypeClasses) {

        return new Function<SyntheticCreationalContext<FaultTypeClasses>, FaultTypeClasses>() {

            @Override
            public FaultTypeClasses apply(SyntheticCreationalContext<FaultTypeClasses> faultTypeClassesSyntheticCreationalContext) {
                return new FaultTypeClasses() {

                    @Override
                    public List<Class<? extends Enum>> getFaultTypeClasses() {
                        return faultTypeClasses;
                    }
                };
            }
        };
    }

}
