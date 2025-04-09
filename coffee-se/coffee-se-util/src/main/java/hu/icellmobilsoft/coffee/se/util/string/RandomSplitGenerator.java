package hu.icellmobilsoft.coffee.se.util.string;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Number random split generator
 *
 * @author martin.nagy
 * @author tamas.cserhati
 * @since 2.11.0
 */
public class RandomSplitGenerator {

    private RandomSplitGenerator() {
    }

    /**
     * Splits a number into defined number of partitions randomly. <small>({@code sum(randomSplit(x, any) == x)})</small>
     * <p>
     * For example if we want to split the value {@code 10} into {@code 3} partitions the result can be for example: {@code [7, 1, 2]},
     * {@code [2, 3, 5]}, {@code [5, 4, 1]}, etc...
     * <p>
     * If the {@code partitionCount} is lower than or equal to the {@code base} number the result will be an array with the size of the {@code base}
     * number filled with the value {@code 1L}. (E.g. {@code randomSplit(2, 4)} -> {@code [1, 1]})
     * <p>
     * The result is not in normal distribution, the lower indices will have higher number on average, but it works ok for low
     * {@code partitionCount}s.
     *
     * @param base
     *            the base value which should be split
     * @param partitionCount
     *            how many partitions should be generated (if the base is big enough)
     * @return the array with size {@code min(base, partitionCount)}
     * @throws IllegalArgumentException
     *             if the {@code base} is negative or the {@code partitionCount} is not positive
     */
    public static long[] randomSplit(long base, int partitionCount) {
        if (base < 0) {
            throw new IllegalArgumentException("base must be not negative");
        }
        if (partitionCount < 1) {
            throw new IllegalArgumentException("partitionCount must be positive");
        }

        if (base <= partitionCount) {
            long[] split = new long[(int) base];
            Arrays.fill(split, 1L);
            return split;
        }

        long[] split = new long[partitionCount];
        long spent = 0;
        for (int i = 0; i < partitionCount - 1; i++) {
            int shouldRemainForLater = partitionCount - 1 - i;
            long random = ThreadLocalRandom.current().nextLong(1, base - spent - shouldRemainForLater + 1);
            split[i] = random;
            spent += random;
        }
        split[partitionCount - 1] = base - spent;
        return split;
    }
}
