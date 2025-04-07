package org.master.diploma.git.graph;

import org.master.diploma.git.support.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GraphHelper {


    private static int UN_INIT = -1;

    public static <T extends Vertex> int findBiggestSubSequenceSubgraph(
            Graph<T> first,
            Graph<T> second
    ) {

        // иницилизация массива dp
        List<List<Integer>> dp = new ArrayList<>(
                Collections.nCopies(
                        first.getVertices().size(),
                        new ArrayList<>(
                                Collections.nCopies(
                                        second.getVertices().size(),
                                        UN_INIT
                                )
                        )
                )
        );

        return findBiggestSubSequenceSubgraph(
                dp,
                first.getRoot(),
                second.getRoot(),
                first,
                second
        );
    }

    private static <T extends Vertex> int findBiggestSubSequenceSubgraph(
            List<List<Integer>> dp,
            int u,
            int v,
            Graph<T> first,
            Graph<T> second
    ) {

        if (dp.get(u).get(v) != UN_INIT) {
            return dp.get(u).get(v);
        }

        int relatingValue = 0;
        if (first.getVertex(u)
                .canRelate(second.getVertex(v))
        ) {

            relatingValue = 1;

            List<List<Integer>> matrix = new ArrayList<>(
                    Collections.nCopies(
                            first.getVertices().size(),
                            new ArrayList<>(
                                    Collections.nCopies(
                                            second.getVertices().size(),
                                            0
                                    )
                            )
                    )
            );

            for (var childU : first.getChildrenNumbers(u)) {
                for (var childV : second.getChildrenNumbers(v)) {
                    int tmp = findBiggestSubSequenceSubgraph(
                            dp,
                            childU,
                            childV,
                            first,
                            second
                    );
                    matrix.get(childU).set(childV, tmp);
                }
            }

            var maximumChildWeightList = getMaximumChildWeight(matrix);
            relatingValue += (int) maximumChildWeightList.stream().filter(x -> x > 0).count();
        }

        int other = 0;

        for (var childU : first.getChildrenNumbers(u)) {
            int tmp = findBiggestSubSequenceSubgraph(
                    dp,
                    childU,
                    v,
                    first,
                    second
            );
            other = Math.max(other, tmp);
        }

        for (var childV : first.getChildrenNumbers(v)) {
            int tmp = findBiggestSubSequenceSubgraph(
                    dp,
                    u,
                    childV,
                    first,
                    second
            );
            other = Math.max(other, tmp);
        }

        dp.get(u).set(v, Math.max(relatingValue, other));
        return dp.get(u).get(v);
    }

    private static List<Integer> getMaximumChildWeight(List<List<Integer>> matrix) {

        reductionMatrix(matrix);
        List<List<Integer>> newMatrix = new ArrayList<>( // 0-индексация будет заменена на 1 индексацию
                Collections.nCopies(
                        matrix.size() + 1,
                        new ArrayList<>(Collections.nCopies(matrix.get(0).size() + 1, 0))
                )
        );

        for (int i = 1; i < matrix.size(); i++) {
            for (int j = 1; j < matrix.get(i).size(); j++) {
                newMatrix.get(i).set(j, matrix.get(i - 1).get(j - 1) + 1);
            }
        }

        List<Integer> temp = HungarianAlgorithm(newMatrix);
        List<Integer> result = new ArrayList<>();

        for (int i = 1; i < temp.size(); i++) { // делаем сдвиг влево
            result.add(temp.get(i) - 1);
        }

        return result;
    }

    private static void reductionMatrix(List<List<Integer>> matrix) {

        matrix.forEach(
                row -> {
                    int max = Collections.max(row);

                    for (int i = 0; i < row.size(); i++) {
                        row.set(i, max - row.get(i));
                    }
                }
        );
    }

    /**
     * Венгерский алгоритм источник:http://www.e-maxx-ru.1gb.ru/algo/assignment_hungary#5
     */
    private static List<Integer> HungarianAlgorithm(List<List<Integer>> a) {
        int n = a.size() - 1;
        int m = a.get(0).size() - 1;
        List<Integer> u = new ArrayList<>(Collections.nCopies(n + 1, 0)); // Инициализируем нулями
        List<Integer> v = new ArrayList<>(Collections.nCopies(m + 1, 0)); // Инициализируем нулями
        List<Integer> p = new ArrayList<>(Collections.nCopies(m + 1, 0)); // Инициализируем нулями
        List<Integer> way = new ArrayList<>(Collections.nCopies(m + 1, 0)); // Инициализируем нулями
        for (int i = 1; i <= n; ++i) {
            p.set(0, i);
            int j0 = 0;
            List<Integer> minv = new ArrayList<>(Collections.nCopies(m + 1, Constants.INFINITY));
            List<Boolean> used = new ArrayList<>(Collections.nCopies(m + 1, false));
            do {
                used.set(j0, true);
                int i0 = p.get(j0);
                int delta = Constants.INFINITY;
                int j1 = -1;

                for (int j = 1; j <= m; ++j) {
                    if (!used.get(j)) {
                        int cur = a.get(i0).get(j) - u.get(i0) - v.get(j);
                        if (cur < minv.get(j)) {
                            minv.set(j, cur);
                            way.set(j, j0);
                        }
                        if (minv.get(j) < delta) {
                            delta = minv.get(j);
                            j1 = j;
                        }
                    }
                }

                for (int j = 0; j <= m; ++j) {
                    if (used.get(j)) {
                        u.set(p.get(j), u.get(p.get(j)) + delta);
                        v.set(j, v.get(j) - delta);
                    } else {
                        minv.set(j, minv.get(j) - delta);
                    }
                }
                j0 = j1;
            } while (p.get(j0) != 0);

            do {
                int j1 = way.get(j0);
                p.set(j0, p.get(j1));
                j0 = j1;
            } while (j0 != 0);
        }

        List<Integer> ans = new ArrayList<>(Collections.nCopies(n + 1, UN_INIT));
        for (int j = 1; j <= m; ++j) {
            if (p.get(j) <= n) {  // Проверка, чтобы избежать IndexOutOfBoundsException
                ans.set(p.get(j), j);
            }
        }
        return ans;
    }
}
