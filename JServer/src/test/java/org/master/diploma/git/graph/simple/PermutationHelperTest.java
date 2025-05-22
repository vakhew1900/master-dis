package org.master.diploma.git.graph.simple;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.master.diploma.git.support.PermutationHelper;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class PermutationHelperTest {

    @Test
    @DisplayName("Test with a valid matching where all permutations are possible")
    void testValidMatchingAllPossible() {
        int n = 3;
        int k = 2;
        Map<Integer, Set<Integer>> verticesMatching = new HashMap<>();
        verticesMatching.put(1, Set.of(1, 2, 3));
        verticesMatching.put(2, Set.of(1, 2, 3));

        List<List<Integer>> permutations = PermutationHelper.generatePermutations(n, k, verticesMatching);
        System.out.println(permutations);

        int expectedSize = 13;
        assertEquals(expectedSize, permutations.size());

        assertTrue(permutations.contains(Arrays.asList(1, 2)));
        assertTrue(permutations.contains(Arrays.asList(1, 3)));
        assertTrue(permutations.contains(Arrays.asList(2, 1)));
        assertTrue(permutations.contains(Arrays.asList(2, 3)));
        assertTrue(permutations.contains(Arrays.asList(3, 1)));
        assertTrue(permutations.contains(Arrays.asList(3, 2)));

        assertTrue(permutations.contains(Arrays.asList(1, -1)));
        assertTrue(permutations.contains(Arrays.asList(2, -1)));
        assertTrue(permutations.contains(Arrays.asList(3, -1)));
        assertTrue(permutations.contains(Arrays.asList(-1, -1)));
        assertTrue(permutations.contains(Arrays.asList(-1, 1)));
        assertTrue(permutations.contains(Arrays.asList(-1, 2)));
        assertTrue(permutations.contains(Arrays.asList(-1, 3)));
    }

    @Test
    @DisplayName("Test with a matching where no permutations are possible")
    void testNoPossiblePermutations() {
        int n = 3;
        int k = 2;
        Map<Integer, Set<Integer>> verticesMatching = new HashMap<>();
        verticesMatching.put(1, Collections.emptySet()); // No vertices are allowed in position 1
        verticesMatching.put(2, Collections.emptySet()); // No vertices are allowed in position 2

        List<List<Integer>> permutations = PermutationHelper.generatePermutations(n, k, verticesMatching);

        int expectedSize = 1;
        assertTrue(permutations.contains(Arrays.asList(-1, -1)));
    }

    @Test
    @DisplayName("Test with a matching where only some permutations are possible")
    void testSomePossiblePermutations() {
        int n = 3;
        int k = 2;
        Map<Integer, Set<Integer>> verticesMatching = new HashMap<>();
        verticesMatching.put(1, Set.of(1, 2)); // Only 1 and 2 allowed in position 1
        verticesMatching.put(2, Set.of(3));    // Only 3 allowed in position 2

        List<List<Integer>> permutations = PermutationHelper.generatePermutations(n, k, verticesMatching);

        int expectedSize = 6;
        assertEquals(expectedSize, permutations.size());

        assertTrue(permutations.contains(Arrays.asList(1, 3)));
        assertTrue(permutations.contains(Arrays.asList(2, 3)));

        assertTrue(permutations.contains(Arrays.asList(1, -1)));
        assertTrue(permutations.contains(Arrays.asList(2, -1)));
        assertTrue(permutations.contains(Arrays.asList(-1, 3)));
        assertTrue(permutations.contains(Arrays.asList(-1, -1)));
    }

    @Test
    @DisplayName("Test with n=4, k=3, and a complex matching")
    void testComplexMatching() {
        int n = 4;
        int k = 3;
        Map<Integer, Set<Integer>> verticesMatching = new HashMap<>();
        verticesMatching.put(1, Set.of(1, 2));
        verticesMatching.put(2, Set.of(3, 4));
        verticesMatching.put(3, Set.of(1));

        List<List<Integer>> permutations = PermutationHelper.generatePermutations(n, k, verticesMatching);

        int expectedSize = 15;
        assertEquals(expectedSize, permutations.size());

        assertTrue(permutations.contains(Arrays.asList(1, 3, -1)));
        assertTrue(permutations.contains(Arrays.asList(1, 4, -1)));
        assertTrue(permutations.contains(Arrays.asList(1, -1, -1)));
        assertTrue(permutations.contains(Arrays.asList(2, 3, 1)));
        assertTrue(permutations.contains(Arrays.asList(2, 3, -1)));
        assertTrue(permutations.contains(Arrays.asList(2, 4, 1)));
        assertTrue(permutations.contains(Arrays.asList(2, 4, -1)));
        assertTrue(permutations.contains(Arrays.asList(2, -1, 1)));
        assertTrue(permutations.contains(Arrays.asList(2, -1, -1)));
        assertTrue(permutations.contains(Arrays.asList(-1, 3, 1)));
        assertTrue(permutations.contains(Arrays.asList(-1, 3, -1)));
        assertTrue(permutations.contains(Arrays.asList(-1, 4, 1)));
        assertTrue(permutations.contains(Arrays.asList(-1, 4, -1)));
        assertTrue(permutations.contains(Arrays.asList(-1, -1, 1)));
        assertTrue(permutations.contains(Arrays.asList(-1, -1, -1)));
    }

    @Test
    @DisplayName("Test with k = n, full permutation with matching")
    void testFullPermutationWithMatching() {
        int n = 3;
        int k = 3;
        Map<Integer, Set<Integer>> verticesMatching = new HashMap<>();
        verticesMatching.put(1, Set.of(1, 2));
        verticesMatching.put(2, Set.of(3, 2));
        verticesMatching.put(3, Set.of(1, 2));

        List<List<Integer>> permutations = PermutationHelper.generatePermutations(n, k, verticesMatching);

        int expectedSize = 17;

        assertEquals(expectedSize, permutations.size());
        assertTrue(permutations.contains(Arrays.asList(1, 2, -1)));
        assertTrue(permutations.contains(Arrays.asList(1, 3, 2)));
        assertTrue(permutations.contains(Arrays.asList(1, 3, -1)));
        assertTrue(permutations.contains(Arrays.asList(1, -1, 2)));
        assertTrue(permutations.contains(Arrays.asList(1, -1, -1)));
        assertTrue(permutations.contains(Arrays.asList(2, 3, 1)));
        assertTrue(permutations.contains(Arrays.asList(2, 3, -1)));
        assertTrue(permutations.contains(Arrays.asList(2, -1, 1)));
        assertTrue(permutations.contains(Arrays.asList(2, -1, -1)));
        assertTrue(permutations.contains(Arrays.asList(-1, 2, 1)));
        assertTrue(permutations.contains(Arrays.asList(-1, 2, -1)));
        assertTrue(permutations.contains(Arrays.asList(-1, 3, 1)));
        assertTrue(permutations.contains(Arrays.asList(-1, 3, 2)));
        assertTrue(permutations.contains(Arrays.asList(-1, 3, -1)));
        assertTrue(permutations.contains(Arrays.asList(-1, -1, 1)));
        assertTrue(permutations.contains(Arrays.asList(-1, -1, 2)));
        assertTrue(permutations.contains(Arrays.asList(-1, -1, -1)));
    }

    @Test
    @DisplayName("Test with empty verticesMatching Map")
    void testEmptyVerticesMatching() {
        int n = 3;
        int k = 2;
        Map<Integer, Set<Integer>> verticesMatching = new HashMap<>();

        assertThrows(NullPointerException.class, () -> {
            PermutationHelper.generatePermutations(n, k, verticesMatching);
        });
    }

    @Test
    @DisplayName("Test with vertexMatching contains Null values")
    void testVertexMatchingContainsNullValues() {
        int n = 3;
        int k = 2;

        Map<Integer, Set<Integer>> verticesMatching = new HashMap<>();
        verticesMatching.put(1, Set.of(1, 2, 3));
        verticesMatching.put(2, null);

        assertThrows(NullPointerException.class, () -> PermutationHelper.generatePermutations(n, k, verticesMatching));
    }

    @Test
    @DisplayName("Test illegal arguments")
    void testIllegalArguments() {
        int n = 3;
        int k = 4;
        Map<Integer, Set<Integer>> verticesMatching = new HashMap<>();
        verticesMatching.put(1, Set.of(1, 2, 3));
        verticesMatching.put(2, Set.of(1, 2, 3));

        assertThrows(IllegalArgumentException.class, () -> PermutationHelper.generatePermutations(n, k, verticesMatching));
    }
}

