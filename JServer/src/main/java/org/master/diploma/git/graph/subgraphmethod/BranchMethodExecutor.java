package org.master.diploma.git.graph.subgraphmethod;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.master.diploma.git.graph.Graph;
import org.master.diploma.git.graph.GraphCompareResult;
import org.master.diploma.git.graph.Vertex;
import org.master.diploma.git.graph.label.LabelVertex;
import org.master.diploma.git.label.Label;
import org.master.diploma.git.support.Multisets;

import java.util.*;

public class BranchMethodExecutor extends SubgraphMethodExecutor {

    @Override
    public <T extends LabelVertex<?>> GraphCompareResult execute(Graph<T> first, Graph<T> second) {

        List<List<T>> firstAllBranches = getAllBranches(first);
        List<List<T>> secondAllBranches = getAllBranches(second);


        List<BranchMatch<T>> branchMatches = getBranchMatches(firstAllBranches, secondAllBranches);


        return new GraphCompareResult();
    }

    private <T extends LabelVertex<?>> List<BranchMatch<T>> getBranchMatches(
            List<List<T>> firstAllBranches,
            List<List<T>> secondAllBranches
    ) {
        List<BranchMatch<T>> branchMatches = new ArrayList<>();

        for (var firstBranch : firstAllBranches) {
            for (var secondBranch : secondAllBranches) {
                branchMatches.add(new BranchMatch<>(firstBranch, secondBranch));
            }
        }

        branchMatches.sort(Comparator.comparing(BranchMatch::getPercentageMatch));
        Collections.reverse(branchMatches);



        return branchMatches;
    }


    private <T extends Vertex> List<List<T>> getAllBranches(Graph<T> graph) {

        List<List<T>> allBranches = new ArrayList<>();
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
            List<List<T>> allBranches
    ) {

        branch.add(cur);
        if (graph.getChildren(cur.getNumber()).isEmpty()) {
            allBranches.add(new ArrayList<>(branch));
            return;
        }

        for (var child : graph.getChildren(cur.getNumber())) {
            getAllBranches(child, graph, branch, allBranches);
        }

        branch.remove(branch.size() - 1);
    }


    @Getter
    @Setter
    @AllArgsConstructor
    private static class BranchMatch<T extends LabelVertex<?>> {
        private double percentageMatch;
        private List<T> firstBranch;
        private List<T> secondBranch;

        public BranchMatch(List<T> firstBranch, List<T> secondBranch) {
            Multiset<Label> firstLabels = HashMultiset.create();
            Multiset<Label> secondLabels = HashMultiset.create();

            firstBranch
                    .stream()
                    .map(vertex -> vertex.getLabels())
                    .flatMap(List::stream)
                    .forEach(firstLabels::add);

            secondBranch
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
        int minSize = Math.min(firstLabels.size(), secondLabels.size());

        // Предотвращаем деление на ноль, если оба Multiset пусты или один из них пуст.
        double percentageMatch = (minSize == 0) ? 0.0 : (double) intersectionSize / minSize;

        return percentageMatch;
    }
}
