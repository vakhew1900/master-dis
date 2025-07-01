package org.master.diploma.git.graph.subgraphmethod;

import lombok.EqualsAndHashCode;
import org.master.diploma.git.graph.Graph;
import org.master.diploma.git.graph.GraphCompareResult;
import org.master.diploma.git.graph.Vertex;
import org.master.diploma.git.graph.label.LabelVertex;
import org.master.diploma.git.support.Constants;
import org.master.diploma.git.support.Creator;
import org.master.diploma.git.support.TwoOrderedMap;

import java.util.*;

public class DpMethodHelper extends SubgraphMethodExecutor {


    private static final int UN_INIT = 0;

    @Override
    public <T extends LabelVertex<?>> GraphCompareResult execute(Graph<T> first, Graph<T> second) {
        return findBiggestSubSequenceSubgraph(first, second).toGraphCompareResult();
    }

    public static <T extends LabelVertex<?>> DpElement findBiggestSubSequenceSubgraph(
            Graph<T> first,
            Graph<T> second
    ) {

        // иницилизация массива dp

        int rowCount = first.getVertices().stream().max(Comparator.comparingInt(Vertex::getNumber)).get().getNumber() + 1;
        int colCount = second.getVertices().stream().max(Comparator.comparingInt(Vertex::getNumber)).get().getNumber() + 1;

        List<List<DpElement>> dp = Creator.createMatrix(
                rowCount,
                colCount,
                DpElement::new
        );


        return findBiggestSubSequenceSubgraph(
                dp,
                first.getRoot(),
                second.getRoot(),
                first,
                second
        );
    }

    private static <T extends LabelVertex<?>> DpElement findBiggestSubSequenceSubgraph(
            List<List<DpElement>> dp,
            int u,
            int v,
            Graph<T> first,
            Graph<T> second
    ) {

        if (dp.get(u).get(v).getWeight() != UN_INIT) {
            return dp.get(u).get(v);
        }

        DpElement relatingValue = new DpElement();
        if (first.getVertex(u)
                .canRelate(second.getVertex(v))
        ) {

            relatingValue = new DpElement();
            relatingValue.add(u, v);

            if (!first.getChildrenNumbers(u).isEmpty() && !second.getChildren(v).isEmpty()) {
                TwoOrderedMap<Integer, Integer> rows = new TwoOrderedMap<>();
                TwoOrderedMap<Integer, Integer> cols = new TwoOrderedMap<>();

                for (var childU : first.getChildrenNumbers(u)) {
                    rows.put(rows.size(), childU);
                    for (var childV : second.getChildrenNumbers(v)) {
                        if (!cols.containsValue(childV)) {
                            cols.put(cols.size(), childV);
                        }
                    }
                }


                List<List<Integer>> weightMatrix = Creator.createMatrix(
                        rows.size(),
                        cols.size(),
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
                        weightMatrix.get(rows.getKey(childU)).set(cols.getKey(childV), tmp.getWeight());
                    }
                }

                var maximumChildWeightList = getMaximumChildWeight(weightMatrix);

                Map<Integer, Integer> firstGraphChildToSecondGraphChild = new HashMap<>(); // нужно для более простого дебага.

                for (int row = 0; row < weightMatrix.size(); row++) {
                    int col = maximumChildWeightList.get(row);
                    int i = rows.get(row);
                    if (cols.size() > col) {
                        int j = cols.get(col);
                        firstGraphChildToSecondGraphChild.put(i, j);
                    }
                }

                DpElement finalRelatingValue = relatingValue;
                firstGraphChildToSecondGraphChild.forEach(
                        (key, value) -> {
                            if (dp.get(key).get(value).getWeight() > 0) {
                                finalRelatingValue.addDpElement(
                                        dp.get(key).get(value)
                                );
                            }
                        }
                );

            }

        }

        DpElement other = new DpElement();

        for (var childU : first.getChildrenNumbers(u)) {
            DpElement tmp = findBiggestSubSequenceSubgraph(
                    dp,
                    childU,
                    v,
                    first,
                    second
            );
            other.fillLabelError(first, second);
            tmp.fillLabelError(first, second);
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
            other.fillLabelError(first, second);
            tmp.fillLabelError(first, second);
            other = DpElement.max(other, tmp);
        }

        relatingValue.fillLabelError(first, second);
        other.fillLabelError(first, second);
        dp.get(u).set(v, DpElement.max(relatingValue, other));
        return dp.get(u).get(v);
    }

    private static List<Integer> getMaximumChildWeight(List<List<Integer>> matrix) {

        int size = Math.max(matrix.size(), matrix.get(0).size()) + 1;
        List<List<Integer>> newMatrix = Creator.createMatrix(
                size,
                size,
                () -> 0
        );

        for (int i = 1; i <= matrix.size(); i++) {
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

                if (j1 == -1) {
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


    @EqualsAndHashCode
    public static class DpElement extends GraphCompareResult {

        public int getWeight() {
            return getMatchingVertices().size();
        }


        public static DpElement max(DpElement first, DpElement second) {
            return (first.isBigger(second)) ? first : second;
        }

        public void addDpElement(DpElement other) {
            this.getMatchingVertices().putAll(other.getMatchingVertices());
        }

        public void add(int first, int second) {
            getMatchingVertices().put(first, second);
        }

        public GraphCompareResult toGraphCompareResult() {
            return GraphCompareResult
                    .builder().
                    matchingVertices(getMatchingVertices())
                    .labelErrors(getLabelErrors())
                    .build();

        }
    }
}
