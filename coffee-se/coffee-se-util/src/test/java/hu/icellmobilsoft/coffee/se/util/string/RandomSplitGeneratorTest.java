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
package hu.icellmobilsoft.coffee.se.util.string;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

/**
 * @author martin.nagy
 * @author tamas.cserhati
 * @since 2.11.0
 */
class RandomSplitGeneratorTest {

    @Test
    void randomSplitSumShouldBeEqualToBase() {
        assertThrows(Exception.class, () -> RandomSplitGenerator.randomSplit(-1, 2));
        assertThrows(Exception.class, () -> RandomSplitGenerator.randomSplit(10, 0));
        assertArrayEquals(new long[] {}, RandomSplitGenerator.randomSplit(0, 3));
        assertArrayEquals(new long[] { 42 }, RandomSplitGenerator.randomSplit(42, 1));
        assertArrayEquals(new long[] { 1, 1 }, RandomSplitGenerator.randomSplit(2, 3));
        assertArrayEquals(new long[] { 1, 1, 1 }, RandomSplitGenerator.randomSplit(3, 3));
        assertEquals(10, sum(RandomSplitGenerator.randomSplit(10, 3)));
        assertEquals(10, sum(RandomSplitGenerator.randomSplit(10, 3)));
        assertEquals(100, sum(RandomSplitGenerator.randomSplit(100, 2)));
        assertEquals(100, sum(RandomSplitGenerator.randomSplit(100, 2)));
        assertEquals(100, sum(RandomSplitGenerator.randomSplit(100, 3)));
        assertEquals(100, sum(RandomSplitGenerator.randomSplit(100, 3)));
        assertEquals(100, sum(RandomSplitGenerator.randomSplit(100, 10)));
        assertEquals(100, sum(RandomSplitGenerator.randomSplit(100, 10)));
        assertEquals(100, sum(RandomSplitGenerator.randomSplit(100, 50)));
        assertEquals(100, sum(RandomSplitGenerator.randomSplit(100, 50)));
    }

    private long sum(long[] array) {
        return Arrays.stream(array).sum();
    }
}
