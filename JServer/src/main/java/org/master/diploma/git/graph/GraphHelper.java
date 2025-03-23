package org.master.diploma.git.graph;

import org.master.diploma.git.support.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GraphHelper {


    private List<Integer> getMaximumChildWeight(List<List<Integer>> matrix) {
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

    /**
     * Венгерский алгоритм источник:http://www.e-maxx-ru.1gb.ru/algo/assignment_hungary#5
     */
    private List<Integer> HungarianAlgorithm(List<List<Integer>> a) {
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
                    if (used.get(j)) {
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

        List<Integer> ans = new ArrayList<>(Collections.nCopies(n + 1, 0));
        for (int j = 1; j <= m; ++j) {
            if (p.get(j) <= n) {  // Проверка, чтобы избежать IndexOutOfBoundsException
                ans.set(p.get(j), j);
            }
        }
        return ans;
    }
}
