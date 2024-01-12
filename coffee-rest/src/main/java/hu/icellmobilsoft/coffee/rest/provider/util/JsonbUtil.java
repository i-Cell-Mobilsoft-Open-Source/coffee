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
package hu.icellmobilsoft.coffee.rest.provider.util;


import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import hu.icellmobilsoft.coffee.rest.provider.FieldOnlyVisibilityStrategy;

/**
 * {@link Jsonb} object creator util class.
 *
 * @author speter555
 * @since 2.5.0
 */
public class JsonbUtil {

    /**
     * Create {@link Jsonb} instance with {@link FieldOnlyVisibilityStrategy} property visibility strategy.
     * 
     * @return configured {@link Jsonb} instance
     */
    public static Jsonb getContext() {
        JsonbConfig config = new JsonbConfig()
                // property visibility strategy setting
                .withPropertyVisibilityStrategy(new FieldOnlyVisibilityStrategy());
        return JsonbBuilder.newBuilder().withConfig(config).build();
    }
}
