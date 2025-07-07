package org.master.diploma.git.support;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.master.diploma.git.graph.subgraphmethod.BruteForceMethodExecutor;

import java.util.*;

public class CombinatoricHelper {

    private static final Logger LOG = LogManager.getLogger(CombinatoricHelper.class);

    public static long factorial(long cnt) {
        int result = 1;

        for (int i = 1; i <= cnt; i++) {
            result *= i;
        }

        return result;
    }

    public static List<List<Integer>> generatePermutations(int n, int k) {
        if (k < 0 || k > n) {
            throw new IllegalArgumentException(String.format("Illegal n = %d and k = %d", n, k));
        }

        List<List<Integer>> result = new ArrayList<>();
        List<Integer> currentPermutation = new ArrayList<>();
        List<Boolean> used = new ArrayList<>(Collections.nCopies(n + 1, false));

        generatePermutationsRecursive(
                n,
                k,
                currentPermutation,
                used,
                result,
                true
        );

        return result;
    }

    private static void generatePermutationsRecursive(
            int n,
            int k,
            List<Integer> currentPermutation,
            List<Boolean> used,
            List<List<Integer>> result,
            boolean notUsedVerticesMatching
    ) {
        if (currentPermutation.size() == k) {
            result.add(new ArrayList<>(currentPermutation));
            return;
        }


        for (int i = 1; i <= n; i++) {
            if (!used.get(i)) {
                used.set(i, true);
                currentPermutation.add(i);
                generatePermutationsRecursive(
                        n,
                        k,
                        currentPermutation,
                        used,
                        result,
                        notUsedVerticesMatching
                );
                currentPermutation.remove(currentPermutation.size() - 1);
                used.set(i, false);
            }

        }
    }


    /**
     * Генерирует все сочетания из n элементов по k (без повторений, порядок не важен).
     *
     * @param n Общее количество элементов (от 1 до n).
     * @param k Размер сочетания (должно быть 0 ≤ k ≤ n).
     * @return Список всех сочетаний.
     * @throws IllegalArgumentException Если k < 0 или k > n.
     */
    public static List<List<Integer>> generateCombinations(int n, int k) {
        if (k < 0 || k > n) {
            throw new IllegalArgumentException(String.format("Illegal n = %d and k = %d", n, k));
        }

        List<List<Integer>> result = new ArrayList<>();
        List<Integer> currentCombination = new ArrayList<>();

        generateCombinationsRecursive(
                n,
                k,
                1,  // Начинаем с 1 (если нужно с 0, замените на 0)
                currentCombination,
                result
        );

        return result;
    }

    private static void generateCombinationsRecursive(
            int n,
            int k,
            int start,
            List<Integer> currentCombination,
            List<List<Integer>> result
    ) {
        if (currentCombination.size() == k) {
            result.add(new ArrayList<>(currentCombination));
            return;
        }

        Timeout.getInstance().brokeTime(BruteForceMethodExecutor.TIME_OUT);

        for (int i = start; i <= n; i++) {
            currentCombination.add(i);
            generateCombinationsRecursive(
                    n,
                    k,
                    i + 1,  // Чтобы избежать повторов, переходим к следующему числу
                    currentCombination,
                    result
            );
            currentCombination.remove(currentCombination.size() - 1);
        }
    }

}

