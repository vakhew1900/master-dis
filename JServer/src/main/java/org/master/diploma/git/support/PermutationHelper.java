package org.master.diploma.git.support;

import java.util.*;

public class PermutationHelper {

    public static List<List<Integer>> generatePermutations(int n, int k, Map<Integer, Set<Integer>> verticesMatching) {
        if (k < 0 || k > n) {
            throw new IllegalArgumentException(String.format("Illegal n = %d and k = %d", n, k));
        }

        List<List<Integer>> result = new ArrayList<>();
        List<Integer> currentPermutation = new ArrayList<>();
        List<Boolean> used = new ArrayList<>(Collections.nCopies(n + 1, false));

        generatePermutationsRecursive(n, k, currentPermutation, used, result, verticesMatching);

        return result;
    }

    private static void generatePermutationsRecursive(
            int n,
            int k,
            List<Integer> currentPermutation,
            List<Boolean> used,
            List<List<Integer>> result,
            Map<Integer, Set<Integer>> verticesMatching
    ) {
        if (currentPermutation.size() == k) {
            result.add(new ArrayList<>(currentPermutation));
            return;
        }

        for (int i = 1; i <= n; i++) {
            if (!used.get(i)) {
                if (verticesMatching.get(currentPermutation.size() + 1).contains(i)) {
                    used.set(i, true);
                    currentPermutation.add(i);
                    generatePermutationsRecursive(n, k, currentPermutation, used, result, verticesMatching);
                    currentPermutation.remove(currentPermutation.size() - 1);
                    used.set(i, false);
                }
            }
        }
    }
}
