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
package hu.icellmobilsoft.coffee.tool.utils.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * ConfigUtil test
 *
 * @author speter555
 * @since 1.12.0
 */
@DisplayName("Testing ConfigUtil")
public class ConfigUtilTest {

    @Test
    void testConfigUtilSameAllInstance() {
        ConfigUtil configUtil1 = ConfigUtil.getInstance();
        ConfigUtil configUtil2 = ConfigUtil.getInstance();
        Assertions.assertEquals(configUtil1.hashCode(), configUtil2.hashCode());
    }
}
