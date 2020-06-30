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
package hu.icellmobilsoft.coffee.module.mongodb.extension;

import java.lang.reflect.Type;

import javax.enterprise.inject.Vetoed;

import hu.icellmobilsoft.coffee.module.mongodb.service.MongoService;

/**
 * Helper class for MongoExtension
 * 
 * @author czenczl
 *
 */
@Vetoed
public class MongoExtensionUtil {

    /**
     * get mongoService generic type
     * 
     * @param <T>
     * @param type
     * @return
     */
    public static <T> Type getMongoServiceBase(Class<?> type) {
        Class<?> baseType = type;

        // find MongoService parent if exist
        while (baseType != Object.class) {
            if (baseType.getSuperclass() != null && baseType.getSuperclass() == MongoService.class) {
                return baseType.getGenericSuperclass();
            }
            baseType = baseType.getSuperclass();
        }
        return null;
    }

}
