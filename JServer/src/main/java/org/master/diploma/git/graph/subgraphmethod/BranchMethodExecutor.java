package org.master.diploma.git.graph.subgraphmethod;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.master.diploma.git.graph.Branch;
import org.master.diploma.git.graph.Graph;
import org.master.diploma.git.graph.GraphCompareResult;
import org.master.diploma.git.graph.Vertex;
import org.master.diploma.git.graph.label.LabelVertex;
import org.master.diploma.git.label.Label;
import org.master.diploma.git.support.BranchLCSHelper;
import org.master.diploma.git.support.Multisets;

import java.util.*;

public class BranchMethodExecutor extends SubgraphMethodExecutor {

    private static final Logger LOG = LogManager.getLogger(BranchMethodExecutor.class);
    @Override
    public <T extends LabelVertex<?>> GraphCompareResult execute(Graph<T> first, Graph<T> second) {

        List<Branch<T>> firstAllBranches = getAllBranches(first);
        List<Branch<T>> secondAllBranches = getAllBranches(second);


        List<BranchMatch<T>> branchMatches = getBranchMatches(firstAllBranches, secondAllBranches);

        GraphCompareResult graphCompareResult = new GraphCompareResult();

        branchMatches.forEach(
                branchMatch -> {
                    graphCompareResult.add(BranchLCSHelper.findBranchLCS(branchMatch));
                }
        );

        graphCompareResult.addLabelErrors(first, second);
        return graphCompareResult;
    }

    private <T extends LabelVertex<?>> List<BranchMatch<T>> getBranchMatches(
            List<Branch<T>> firstAllBranches,
            List<Branch<T>> secondAllBranches
    ) {
        List<BranchMatch<T>> branchMatches = new ArrayList<>();

        for (var firstBranch : firstAllBranches) {
            for (var secondBranch : secondAllBranches) {
                branchMatches.add(new BranchMatch<>(firstBranch, secondBranch));
            }
        }

        branchMatches.sort(Comparator.comparing(BranchMatch::getPercentageMatch));
        Collections.reverse(branchMatches);


        List<BranchMatch<T>> result = new ArrayList<>();
        Set<UUID> branchIds = new HashSet<>();

        branchMatches.forEach(
                branchMatch -> {
                    if (
                            !branchIds.contains(branchMatch.firstBranch.getUuid()) &&
                                    !branchIds.contains(branchMatch.secondBranch.getUuid())
                    ) {
                        branchIds.add(branchMatch.firstBranch.getUuid());
                        branchIds.add(branchMatch.secondBranch.getUuid());
                        result.add(branchMatch);
                    }
                }
        );


        return result;
    }


    private <T extends Vertex> List<Branch<T>> getAllBranches(Graph<T> graph) {

        List<Branch<T>> allBranches = new ArrayList<>();
        getAllBranches(
                graph.getVertex(graph.getRoot()),
                graph,
                new ArrayList<>(),
                allBranches
        );

        return allBranches;
    }

    private <T extends Vertex> void getAllBranches(
            T cur,
            Graph<T> graph,
            ArrayList<T> branch,
            List<Branch<T>> allBranches
    ) {

        branch.add(cur);
        if (graph.getChildren(cur.getNumber()).isEmpty()) {
            allBranches.add(
                    new Branch<>(
                            UUID.randomUUID(),
                            new ArrayList<>(branch)
                    )
            );

        }

        for (var child : graph.getChildren(cur.getNumber())) {
            getAllBranches(child, graph, branch, allBranches);
        }

        branch.remove(branch.size() - 1);
    }


    @Getter
    @Setter
    @AllArgsConstructor
    public static class BranchMatch<T extends LabelVertex<?>> {
        private double percentageMatch;
        private Branch<T> firstBranch;
        private Branch<T> secondBranch;

        public BranchMatch(Branch<T> firstBranch, Branch<T> secondBranch) {
            Multiset<Label> firstLabels = HashMultiset.create();
            Multiset<Label> secondLabels = HashMultiset.create();

            firstBranch
                    .getVertices()
                    .stream()
                    .map(vertex -> vertex.getLabels())
                    .flatMap(List::stream)
                    .forEach(firstLabels::add);

            secondBranch
                    .getVertices()
                    .stream()
                    .map(vertex -> vertex.getLabels())
                    .flatMap(List::stream)
                    .forEach(secondLabels::add);

            this.firstBranch = firstBranch;
            this.secondBranch = secondBranch;
            this.percentageMatch = calculatePercentageMatch(firstLabels, secondLabels);
        }
    }

    private static <T> double calculatePercentageMatch(Multiset<T> firstLabels, Multiset<T> secondLabels) {
        Multiset<T> intersection = Multisets.intersect(firstLabels, secondLabels);
        int intersectionSize = intersection.size();
        int minSize = Math.max(firstLabels.size(), secondLabels.size());

        // Предотвращаем деление на ноль, если оба Multiset пусты или один из них пуст.
        return (minSize == 0) ? 0.0 : (double) intersectionSize / minSize;
    }
}
