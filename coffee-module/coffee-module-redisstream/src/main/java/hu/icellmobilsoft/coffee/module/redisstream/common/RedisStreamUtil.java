/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2022 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.module.redisstream.common;

import jakarta.enterprise.inject.Vetoed;

/**
 * Util class for the common redis stream functions
 * 
 * @author martin.nagy
 * @since 1.10.0
 */
@Vetoed
public class RedisStreamUtil {

    /**
     * Returns the redis stream key calculated by the stream group
     * 
     * @param streamGroup
     *            the redis stream group
     * @return the redis stream key
     */
    public static String streamKey(String streamGroup) {
        return streamGroup + "Stream";
    }

    private RedisStreamUtil() {
    }
}
