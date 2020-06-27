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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Provider;

import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * MongoDb configuration values from microprofile-config
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Deprecated
@Dependent
public class MongoDbConfigImpl implements MongoDbConfig {

    @SuppressWarnings("cdi-ambiguous-dependency")
    @Inject
    @ConfigProperty(name = "mongodb.uri", defaultValue = "mongodb://user:password@localhost:27017/database?ssl=false")
    private Provider<String> uri;

    @SuppressWarnings("cdi-ambiguous-dependency")
    @Inject
    @ConfigProperty(name = "mongodb.database", defaultValue = "default")
    private Provider<String> database;

    /** {@inheritDoc} */
    @Override
    public String getUri() {
        return uri.get();
    }

    /** {@inheritDoc} */
    @Override
    public String getDatabase() {
        return database.get();
    }
}
