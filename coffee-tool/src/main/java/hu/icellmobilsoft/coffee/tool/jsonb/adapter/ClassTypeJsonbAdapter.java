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
package hu.icellmobilsoft.coffee.tool.jsonb.adapter;

import java.time.YearMonth;

import jakarta.json.bind.JsonbException;
import jakarta.json.bind.adapter.JsonbAdapter;

/**
 * Json &lt;-&gt; Class converter.
 *
 * @author bucherarnold
 * @since 2.9.0
 */
public class ClassTypeJsonbAdapter implements JsonbAdapter<Class<?>, String> {

    @Override
    public String adaptToJson(Class<?> obj) {
        return obj.getName();
    }

    @Override
    public Class<?> adaptFromJson(String obj) {
        try {
            return Class.forName(obj);
        } catch (ClassNotFoundException e) {
            throw new JsonbException(e.getMessage(), e);
        }
    }
}
