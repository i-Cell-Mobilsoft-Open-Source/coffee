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

import java.util.HashMap;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;

import com.mongodb.client.MongoClient;

/**
 * 
 * Container for MongoClient instances
 * 
 * @author czenczl
 * @since 1.1.0
 *
 */
@ApplicationScoped
public class MongoClientContainer {

    private final Map<String, MongoClient> clientMap = new HashMap<>();

    /**
     * Returns the client map
     * 
     * @return the client map
     */
    public Map<String, MongoClient> getClientMap() {
        return clientMap;
    }

    /**
     * Puts entry to the client map
     * 
     * @param key
     *            the key of the entry
     * @param mongoClientWrapper
     *            the value of the entry
     * @return the previous value associated with {@code key}, or {@code null} if there was no mapping for {@code key}. (A {@code null} return can
     *         also indicate that the map previously associated {@code null} with {@code key}.)
     */
    public MongoClient put(String key, MongoClient mongoClientWrapper) {
        return clientMap.put(key, mongoClientWrapper);
    }

}
