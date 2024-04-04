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
package hu.icellmobilsoft.coffee.module.configdoc;

/**
 * Example class for the {@link ConfigDoc} test<br>
 * The generated file will be here: target/test-classes/META-INF/config_keys.adoc
 *
 * @author martin.nagy
 * @since 1.9.0
 */
@ConfigDoc
public interface ConfigDocExample {

    /**
     * test prefix
     */
    @ConfigDoc(exclude = true)
    String PREFIX = "test.";

    /**
     * test2
     */
    String test2 = "test2.xxx";

    /**
     * Lorem ipsum dolor sit amet, consectetur adipisicing elit. Illo, placeat!
     */
    String foo = PREFIX + "foo";

    /**
     * Lorem ipsum dolor sit amet, consectetur adipisicing elit. Iusto, sapiente?
     */
    @ConfigDoc(description = "Override...")
    String bar = PREFIX + "bar";

    /**
     * Lorem ipsum dolor sit amet, consectetur adipisicing elit. Ipsam, similique?
     * 
     * @since 3.14159
     */
    @ConfigDoc(defaultValue = "5000")
    String baz = PREFIX + "baz";

    /**
     * Lorem ipsum dolor sit amet, consectetur adipisicing elit. Ipsam, similique?
     *
     * @since 3.14159
     */
    @ConfigDoc(defaultValue = "999", isStartupParam = true, isRuntimeOverridable = true)
    String features = PREFIX + "features";

    /**
     * Lorem ipsum dolor sit amet, consectetur adipisicing elit. Ipsam, similique?
     *
     * @since 3.14159
     */
    @ConfigDoc(defaultValue = "1234", title = "Title Test")
    String titleTest = PREFIX + "title";
}
