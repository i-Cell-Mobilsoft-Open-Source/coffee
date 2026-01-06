/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2025 i-Cell Mobilsoft Zrt.
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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import jakarta.inject.Inject;

import hu.icellmobilsoft.coffee.module.repserv.api.annotation.RepositoryService;

@RepositoryService
public class ExampleService extends AbstractExampleService<String> {

    @Inject
    private ExampleRepository repository;

    public ExampleService() {
        super();
    }

    public static void staticTest() {
    }

    public Object test(Param param, ParamRecord paramRecord) {
        return wrap(repository::test, param.getProp(), paramRecord.p(), "test", "prop", "p");
    }

    public <T extends BigDecimal&Serializable, R> long getBigDecimal(String param1, T param2, R param3) throws NumberFormatException, NullPointerException {
        System.out.println(param3);
        return repository.count(param1, param2);
    }

    public List<Object> findAll() {
        return repository.findAll();
    }

    public List<Object> findCustom() {
        return List.of();
    }

    @Override
    public Object method1(String param1, String param2) {
        Object result = super.method1(param1, param2);
        System.out.println(result);
        return result;
    }

    @Override
    public String method3(String param1, String param2) {
        return wrap(repository::method3, param1, param2, "method3", "param1", "param2");
    }
}
