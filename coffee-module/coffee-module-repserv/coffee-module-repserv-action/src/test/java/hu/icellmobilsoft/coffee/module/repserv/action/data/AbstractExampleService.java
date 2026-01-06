/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2026 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.module.repserv.action.data;

import java.util.function.BiFunction;

import jakarta.inject.Inject;

public abstract class AbstractExampleService<T> {

    @Inject
    private ExampleRepository repository;

    public AbstractExampleService() {
        super();
    }

    protected <P1, P2, R> R wrap(BiFunction<P1, P2, R> function, P1 param1, P2 param2, String methodName, String p1Name, String p2Name) {
        System.out.println(methodName + " " + " " + p1Name + " " + p2Name + " " + param1 + " " + param2);
        return function.apply(param1, param2);
    }

    public Object method1(String param1, String param2) {
        return wrap(repository::method1, param1, param2, "overriddenMethod1", "param1", "param2");
    }

    public Object method2(String param1, String param2) {
        return wrap(repository::method2, param1, param2, "method2", "param1", "param2");
    }

    public abstract T method3(String param1, String param2);
}
