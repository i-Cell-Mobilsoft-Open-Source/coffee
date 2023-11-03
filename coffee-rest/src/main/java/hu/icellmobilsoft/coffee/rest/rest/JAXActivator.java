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
package hu.icellmobilsoft.coffee.rest.rest;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * JAXActivator is an arbitrary name, what is important is that javax.ws.rs.core.Application is extended and the @ApplicationPath annotation is used
 * with a "rest" path. Without this the rest routes linked to from index.html would not be found.
 *
 * @since 1.0.0
 */
@ApplicationPath("")
public class JAXActivator extends Application {

    /**
     * Default constructor, constructs a new object.
     */
    public JAXActivator() {
        super();
    }

}
