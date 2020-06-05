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
package hu.icellmobilsoft.coffee.tool.utils.stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.OutputStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import hu.icellmobilsoft.coffee.tool.utils.stream.OutputStreamCopier;

/**
 * @author mark.petrenyi
 */
@DisplayName("Testing OutputStreamCopier")
@ExtendWith(MockitoExtension.class)
class OutputStreamCopierTest {

    @Mock
    private OutputStream outputStream;

    @InjectMocks
    private OutputStreamCopier underTest;

    @Test
    @DisplayName("Testing write() actually writes into original stream")
    void write() throws Exception {
        // given
        // when
        underTest.write(1);
        // then
        Mockito.verify(outputStream).write(ArgumentMatchers.eq(1));
    }

    @DisplayName("Testing getCopy()")
    @Nested
    class GetCopyTest {

        @Test
        @DisplayName("Testing getCopy() returns write(int) input")
        void withInt() throws Exception {
            // given
            int expected = 1;
            underTest.write(expected);
            // when
            byte[] actual = underTest.getCopy();
            // then
            assertEquals(expected, actual[0]);
        }

        @Test
        @DisplayName("Testing getCopy() returns write(byte[]) input")
        void copy() throws Exception {
            // given
            byte[] expected = "TEST".getBytes();
            underTest.write(expected);
            // when
            byte[] actual = underTest.getCopy();
            // then
            assertArrayEquals(expected, actual);
        }
    }

}
