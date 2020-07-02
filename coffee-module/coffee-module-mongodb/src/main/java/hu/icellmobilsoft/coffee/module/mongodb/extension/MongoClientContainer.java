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

import javax.enterprise.context.ApplicationScoped;

import com.mongodb.MongoClient;

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

    private Map<String, MongoClient> clientMap = new HashMap<>();

    public Map<String, MongoClient> getClientMap() {
        return clientMap;
    }

    public MongoClient put(String key, MongoClient mongoClientwrapper) {
        return clientMap.put(key, mongoClientwrapper);
    }

}
