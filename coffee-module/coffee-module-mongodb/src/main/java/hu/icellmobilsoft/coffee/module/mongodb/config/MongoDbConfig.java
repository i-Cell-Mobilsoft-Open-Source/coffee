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
package hu.icellmobilsoft.coffee.module.mongodb.config;

/**
 * MongoDb configuration values
 *
 * @author imre.scheffer
 * @since 1.0.0
 * 
 * @deprecated Use {@link hu.icellmobilsoft.coffee.module.mongodb.extension.MongoConfigHelper} instead, forRemoval = true, since = "1.1.0"
 */
@Deprecated(forRemoval = true, since = "1.1.0")
public interface MongoDbConfig {

    /**
     * MongoDb full URI, example: "mongodb://user:password@localhost:27017/database?ssl=false"
     */
    String getUri();

    /**
     * Selected database (collection) in MongoDB, example: "user_request_response"
     */
    String getDatabase();
}
