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
package hu.icellmobilsoft.coffee.module.repserv.action.util;

import java.nio.charset.Charset;

import com.google.common.hash.Hashing;

import hu.icellmobilsoft.coffee.module.repserv.action.data.MethodData;

/**
 * Utility class for generating stable IDs for methods based on their signature.
 * <p>
 * The generated ID is computed from the method name, type parameters and parameter list, and is produced using a Murmur3 32-bit hash to yield a
 * compact identifier.
 * </p>
 *
 * @author janos.boroczki
 * @since 2.12.0
 */
public class IdGenerator {

    /**
     * Prevent instantiation.
     */
    private IdGenerator() {
        super();
    }

    /**
     * Generates a deterministic ID for the supplied {@link MethodData} by hashing the combined method signature string (name + type parameters +
     * parameter list).
     *
     * @param methodData
     *            the method metadata used to build the signature
     * @return a hashed identifier string representing the method signature
     */
    public static String generateId(MethodData methodData) {
        String s = methodData.getMethodName() + methodData.getTypeParamsString() + methodData.getParamsString();
        return Hashing.murmur3_32_fixed().hashString(s, Charset.defaultCharset()).toString();
    }
}
