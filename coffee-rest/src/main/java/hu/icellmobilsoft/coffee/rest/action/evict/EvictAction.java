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
package hu.icellmobilsoft.coffee.rest.action.evict;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Application-scoped default eviction action used by system REST endpoints.
 * Applications can replace it with a subclass annotated with {@code @Alternative}, {@code @Priority} and a CDI scope.
 *
 * @author balazs.bakos
 * @since 4.0.0
 */
@ApplicationScoped
public class EvictAction extends AbstractEvictAction {

    /**
     * Default constructor.
     */
    public EvictAction() {
        // Default constructor for java 21
    }
}
