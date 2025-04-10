package org.master.diploma.git.graph;

import org.master.diploma.git.support.Constants;
import org.master.diploma.git.support.Creator;

import java.util.*;

public class GraphHelper {


    private static int UN_INIT = 0;

    public static <T extends Vertex> DpElement findBiggestSubSequenceSubgraph(
            Graph<T> first,
            Graph<T> second
    ) {

        // иницилизация массива dp

        int rowCount = first.getVertices().stream().max(Comparator.comparingInt(Vertex::getNumber)).get().getNumber() + 1;
        int colCount = second.getVertices().stream().max(Comparator.comparingInt(Vertex::getNumber)).get().getNumber() + 1;

        List<List<DpElement>> dp = Creator.createMatrix(
                rowCount,
                colCount,
                () -> new DpElement(new HashMap<>())
                );


        return findBiggestSubSequenceSubgraph(
                dp,
                first.getRoot(),
                second.getRoot(),
                first,
                second
        );
    }

    private static <T extends Vertex> DpElement findBiggestSubSequenceSubgraph(
            List<List<DpElement>> dp,
            int u,
            int v,
            Graph<T> first,
            Graph<T> second
    ) {

        if (dp.get(u).get(v).getWeight() != UN_INIT) {
            return dp.get(u).get(v);
        }

        DpElement relatingValue = new DpElement( new HashMap<>());
        if (first.getVertex(u)
                .canRelate(second.getVertex(v))
        ) {

            relatingValue = new DpElement(new HashMap<>(Map.of(u,v)));


            List<List<Integer>> weightMatrix = Creator.createMatrix(
                    dp.size(),
                    dp.get(0).size(),
                    () -> 0
                    );



            for (var childU : first.getChildrenNumbers(u)) {
                for (var childV : second.getChildrenNumbers(v)) {
                    DpElement tmp = findBiggestSubSequenceSubgraph(
                            dp,
                            childU,
                            childV,
                            first,
                            second
                    );
                    weightMatrix.get(childU).set(childV, tmp.getWeight());
                }
            }

            var maximumChildWeightList = getMaximumChildWeight(weightMatrix);

            for (int row = 0; row < dp.size(); row++) {
                int col = maximumChildWeightList.get(row);

                if (col >= 0 && col < dp.get(row).size() && dp.get(row).get(col).getWeight() > 0) {
                    relatingValue.addDpElement(
                            dp.get(row).get(col)
                    );
                }
            }

        }

        DpElement other = new DpElement(new HashMap<>());

        for (var childU : first.getChildrenNumbers(u)) {
            DpElement tmp = findBiggestSubSequenceSubgraph(
                    dp,
                    childU,
                    v,
                    first,
                    second
            );
            other = DpElement.max(other, tmp);
        }

        for (var childV : second.getChildrenNumbers(v)) {
            DpElement tmp = findBiggestSubSequenceSubgraph(
                    dp,
                    u,
                    childV,
                    first,
                    second
            );
            other = DpElement.max(other, tmp);
        }

        dp.get(u).set(v, DpElement.max(relatingValue, other));
        return dp.get(u).get(v);
    }

    private static List<Integer> getMaximumChildWeight(List<List<Integer>> matrix) {

        int size = Math.max(matrix.size(), matrix.get(0).size()) + 1;
        List<List<Integer>> newMatrix = Creator.createMatrix(
                size,
                size,
                ()->0
        );

        for (int i = 1; i <= matrix.size() ; i++) {
            for (int j = 1; j <= matrix.get(0).size(); j++) {
                newMatrix.get(i).set(j, matrix.get(i - 1).get(j - 1));
            }
        }

        reductionMatrix(newMatrix);

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

                if (j1 == -1){
                    break;
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


    public static class DpElement {
        private Map<Integer, Integer> matchingVertices = new HashMap<>();

        public DpElement(Map<Integer, Integer> matchingVertices) {

            this.matchingVertices = matchingVertices;
        }

        public int getWeight() {
            return matchingVertices.size();
        }

        public Map<Integer, Integer> getMatchingVertices() {
            return matchingVertices;
        }

        public static boolean isBigger(DpElement first, DpElement second) {
            return first.getWeight() > second.getWeight();
        }

        public static DpElement max(DpElement first, DpElement second) {
           return (isBigger(first, second))? first : second;
        }

        public void addDpElement(DpElement other) {
            this.matchingVertices.putAll(other.getMatchingVertices());
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            DpElement dpElement = (DpElement) o;
            return Objects.equals(matchingVertices, dpElement.matchingVertices);
        }


        @Override
        public int hashCode() {
            return Objects.hash(matchingVertices);
        }
    }
}
