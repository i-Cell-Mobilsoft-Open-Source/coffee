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
package hu.icellmobilsoft.coffee.module.configdoc.data;

import hu.icellmobilsoft.coffee.module.configdoc.ConfigDoc;

/**
 * Data class for the collected {@link ConfigDoc} annotation data.
 *
 * @author martin.nagy
 * @since 1.9.0
 */
public class DocData {
    private final String key;
    private final String source;
    private final String description;
    private final String defaultValue;

    public DocData(String key, String source, String description, String defaultValue) {
        this.key = key;
        this.source = source;
        this.description = description;
        this.defaultValue = defaultValue;
    }

    public String getKey() {
        return key;
    }

    public String getSource() {
        return source;
    }

    public String getDescription() {
        return description;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}
