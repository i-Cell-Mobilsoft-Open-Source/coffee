/*-
 * #%L
 * DookuG
 * %%
 * Copyright (C) 2023 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.rest.system;

import hu.icellmobilsoft.coffee.rest.rest.BaseRestService;

/**
 * Astract System rest endpoint
 *
 * @author tamas.cserhati
 * @since 2.11.0
 */
public abstract class AbstractSystemRest extends BaseRestService implements ISystemRest {

    /**
     * {@value #NEW_LINE}
     */
    protected static final String NEW_LINE = "\n";
    /**
     * {@value #META_INF_MANIFEST_MF}
     */
    protected static final String META_INF_MANIFEST_MF = "META-INF/MANIFEST.MF";
    /**
     * {@value #IMPLEMENTATION_TITLE}
     */
    protected static final String IMPLEMENTATION_TITLE = "Implementation-Title";
    /**
     * {@value #CLASS_PATH}
     */
    protected static final String CLASS_PATH = "Class-Path";

}
