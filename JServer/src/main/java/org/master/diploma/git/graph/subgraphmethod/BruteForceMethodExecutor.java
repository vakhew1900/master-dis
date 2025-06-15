package org.master.diploma.git.graph.subgraphmethod;

import com.google.common.collect.Sets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.master.diploma.git.graph.Graph;
import org.master.diploma.git.graph.GraphCompareResult;
import org.master.diploma.git.graph.Vertex;
import org.master.diploma.git.graph.label.LabelVertex;
import org.master.diploma.git.support.CombinatoricHelper;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BruteForceMethodExecutor extends SubgraphMethodExecutor {

    private static final Logger LOG = LogManager.getLogger(BranchMethodExecutor.class);
    @Override
    public <T extends LabelVertex<?>> GraphCompareResult execute(
            Graph<T> first,
            Graph<T> second
    ) {
        boolean invert = first.getVertices().size() > second.getVertices().size();

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

        GraphCompareResult res = new GraphCompareResult();
        res.setInvert(invert);
        int count = 0;
        for (int i = 1; i <= k; i++) {
            List<List<Integer>> allFirstVerticesPermutation = CombinatoricHelper.generateCombinations(k, i);
            List<List<Integer>> allSecondVerticesPermutation = CombinatoricHelper.generatePermutations(n, i);

            for (var firstPermutation : allFirstVerticesPermutation) {
                for (var secondPermutation : allSecondVerticesPermutation) {
                    boolean canCompare = true;

                    for (int j = 0; j < firstPermutation.size(); j++) {
                        canCompare = canCompare && first
                                .getVertices().get(firstPermutation.get(j) - 1)
                                .canRelate(
                                        second
                                                .getVertices()
                                                .get(secondPermutation.get(j) - 1)
                                );
                    }

                    count++;
                    if (count % 1000000000 == 0)
                        LOG.info(count);
                    Graph<T> finalFirst = first;
                    Graph<T> finalSecond = second;
                    if (canCompare) {
                        GraphCompareResult next = calculate(first, second,
                                firstPermutation
                                        .stream()
                                        .map(pos -> finalFirst.getVertices().get(pos - 1).getNumber())
                                        .collect(Collectors.toList()),
                                secondPermutation
                                        .stream()
                                        .map(pos -> finalSecond.getVertices().get(pos - 1).getNumber())
                                        .collect(Collectors.toList())
                        );
                        next.setInvert(invert);
                        next.fillLabelError(first, second);
                        if (next.isBigger(res)) {
                            res = next;
                        }
                    }
                }
            }
        }

        res.fillLabelError(first, second); //todo поменять
        return res;
    }

    private <T extends Vertex> GraphCompareResult calculate(
            Graph<T> first,
            Graph<T> second,
            List<Integer> firstPermutation,
            List<Integer> secondPermutation
    ) {
        Map<Integer, UUID> firstUUIDS = new HashMap<>();
        Map<Integer, UUID> secondUUIDS = new HashMap<>();

        for (int i = 0; i < firstPermutation.size(); i++) {
            UUID uuid = UUID.randomUUID();
            firstUUIDS.put(firstPermutation.get(i), uuid);
            secondUUIDS.put(secondPermutation.get(i), uuid);
        }

        Set<Map.Entry<UUID, UUID>> firstSet = integerToUUID(firstUUIDS, first);
        Set<Map.Entry<UUID, UUID>> secondSet = integerToUUID(secondUUIDS, second);

        GraphCompareResult result = new GraphCompareResult();
        if (Sets.difference(firstSet, secondSet).isEmpty()) {
            result.setMatchingVertices(
                    IntStream
                            .range(0, firstPermutation.size())
                            .boxed()
                            .collect(Collectors.toMap(
                                            firstPermutation::get,
                                            secondPermutation::get
                                    )
                            )
            );
        }

        return result;
    }

    private <T extends Vertex> Set<Map.Entry<UUID, UUID>> integerToUUID(Map<Integer, UUID> map, Graph<T> graph) {
        return graph.getTransitiveClosure()
                .stream()
                .filter(entry -> map.containsKey(entry.getKey()) && map.containsKey(entry.getValue()))
                .map(entry -> new AbstractMap.SimpleEntry<>(map.get(entry.getKey()), map.get(entry.getValue())))
                .collect(Collectors.toSet());
    }

}
