package org.master.diploma.git.graph.subgraphmethod;

import org.master.diploma.git.graph.Graph;
import org.master.diploma.git.graph.GraphCompareResult;
import org.master.diploma.git.graph.Vertex;
import org.master.diploma.git.support.PermutationHelper;

import java.util.*;

public class BruteForceMethodExecutor extends SubgraphMethodExecutor {

    @Override
    public  <T extends Vertex> GraphCompareResult execute(
            Graph<T> first,
            Graph<T> second
    ) {
        boolean invert = first.getVertices().size() < second.getVertices().size();

        if (invert) {
            var tmp = first;
            first = second;
            second = tmp;
        }


        Map<Integer, Set<Integer>> verticesMatching = new HashMap<>();
        for (T u : first.getVertices()) {
            verticesMatching.put(u.getNumber(), new HashSet<>());

            for (T v : second.getVertices()) {

                if (u.canRelate(v)) {
                    verticesMatching.get(u.getNumber()).add(v.getNumber());
                }
            }
        }

        int n = second.getVertices().size();
        int k = first.getVertices().size();

        List<List<Integer>> allVerticesPermutation = PermutationHelper.generatePermutations(n, k, verticesMatching);


        for (var permutationVertices : allVerticesPermutation) {

        }

        return new GraphCompareResult(); //todo поменять
    }

}
