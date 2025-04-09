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
