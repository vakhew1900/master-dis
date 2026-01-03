package org.master.diploma.git.generator;

import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RandomUtils {
    private static final int SEED = 12345;
    private static final Random RANDOM = new Random(SEED);

    public static int nextInt(int left, int right) {
        validateRange(left, right);
        int x =  RANDOM.nextInt(right - left + 1) + left;
        return  x;
    }

    public static Set<Integer> ints(int left, int right, int count) {
        return IntStream
                .range(0, count)
                .boxed()
                .map(_unused -> nextInt(left, right))
                .collect(Collectors.toSet());
    }


    private static void validateRange(int left, int right) {
        if (left > right) {
            throw new IllegalArgumentException(
                    String.format("Left boundary (%d) must be less than or equal to right boundary (%d)", left, right)
            );
        }
    }

}
