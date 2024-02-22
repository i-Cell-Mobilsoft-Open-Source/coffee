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
package hu.icellmobilsoft.coffee.model.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Testing {@link AbstractEntity} class.
 *
 * @author attila.kiss
 */
@DisplayName("Testing AbstractEntity class")
public class AbstractEntityTest {

    @Test
    @DisplayName("Testing toString method")
    public void toStringTest() {
        // given
        AbstractEntity entity = new DefaultImplAbstractEntity();

        // when
        String actualToStringValue = entity.toString();

        // then
        Assertions.assertTrue(actualToStringValue.contains("blobColumn=Blob"));
        Assertions.assertTrue(actualToStringValue.contains("byteArrayColumn=byte[]"));
        Assertions.assertTrue(actualToStringValue.contains("clobColumn=Clob"));
        Assertions.assertTrue(actualToStringValue.contains("blobStream=InputStream"));
    }

}
