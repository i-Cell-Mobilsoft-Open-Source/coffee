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
package hu.icellmobilsoft.coffee.module.docgen.config;

/**
 * Example class for the {@link DynamicConfigTemplate} and {@link DynamicConfigDocs} test<br>
 * The generated file will be here: target/test-classes/META-INF/config_keys.adoc
 *
 * @author mark.petrenyi
 * @since 1.9.0
 */
@ConfigDoc
@DynamicConfigTemplate
public interface DynamicConfigTemplateExample {

    /**
     * test prefix
     */
    @ConfigDoc(exclude = true)
    String PREFIX = "test.";

    /**
     * Lorem ipsum dolor sit amet, consectetur adipisicing elit. Illo, placeat!
     */
    String foo = PREFIX + "{0}.foo";
}
