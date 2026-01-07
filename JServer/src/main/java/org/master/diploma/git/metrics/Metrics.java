package org.master.diploma.git.metrics;

import com.google.common.collect.Sets;
import de.vandermeer.asciitable.AsciiTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.master.diploma.git.graph.Graph;
import org.master.diploma.git.graph.GraphCompareResult;
import org.master.diploma.git.graph.label.LabelVertex;
import org.master.diploma.git.label.SimpleLabel;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class Metrics {

    private int tp;
    private int tn;
    private int fp;
    private int fn;


    public void add(Metrics metrics) {
        this.tp += metrics.tp;
        this.tn += metrics.tn;
        this.fp += metrics.fp;
        this.fn += metrics.fn;
    }


    public static double precision(int tp, int fp) {
        return tp + fp == 0 ? 0 : (double) tp / (tp + fp);
    }

    public static double recall(int tp, int fn) {
        return tp + fn == 0 ? 0 : (double) tp / (tp + fn);
    }

    public static double f1(double precision, double recall) {
        return precision + recall == 0 ? 0 :
                2 * precision * recall / (precision + recall);
    }

    public double precision() {
        return precision(tp, fp);
    }

    public double recall() {
        return recall(tp, fn);
    }

    public double f1() {
        return f1(precision(), recall());
    }

    public String toCompactTable() {
        StringBuilder sb = new StringBuilder();

        // Таблица 1: Confusion Matrix
        AsciiTable confusionTable = new AsciiTable();
        confusionTable.addRule();
        confusionTable.addRow("Confusion Matrix", "Predicted Positive", "Predicted Negative");
        confusionTable.addRule();
        confusionTable.addRow("Actual Positive", tp, fn);
        confusionTable.addRow("Actual Negative", fp, tn);
        confusionTable.addRule();

        sb.append(confusionTable.render());
        sb.append("\n\n");

        // Таблица 2: Metrics
        AsciiTable metricsTable = new AsciiTable();
        metricsTable.addRule();
        metricsTable.addRow("Metric", "Value");
        metricsTable.addRule();
        metricsTable.addRow("Precision", String.format("%.4f", precision()));
        metricsTable.addRow("Recall", String.format("%.4f", recall()));
        metricsTable.addRow("F1-Score", String.format("%.4f", f1()));
//            metricsTable.addRow("Accuracy", String.format("%.4f", accuracy()));
        metricsTable.addRule();

        sb.append(metricsTable.render());

        return sb.toString();
    }



    public static <T extends  Graph<W>, W extends LabelVertex<SimpleLabel>> Metrics findMetric(
            T first,
            T second,
            GraphCompareResult expectedGraphCompared,
            GraphCompareResult result)
    {
        Set<Pair<Integer, Integer>> allPairs = new HashSet<>();

        for(var firstVertex : first.getVertices()) {
            for (var secondVertex : second.getVertices()) {
                allPairs.add(new ImmutablePair<>(firstVertex.getNumber(), secondVertex.getNumber()));
            }
        }

        Set<Pair<Integer, Integer>> expectedPairs = expectedGraphCompared
                .getMatchingVertices()
                .entrySet()
                .stream()
                .map(entry -> new ImmutablePair<>(entry.getKey(), entry.getValue()))
                .collect(Collectors.toSet());

        Set<Pair<Integer, Integer>> resultPairs = result
                .getMatchingVertices()
                .entrySet()
                .stream()
                .map(entry -> new ImmutablePair<>(entry.getKey(), entry.getValue()))
                .collect(Collectors.toSet());

        var intersection = Sets.intersection(expectedPairs, resultPairs);
        int tp = intersection.size();
        int fp = Sets.difference(resultPairs, expectedPairs).size();
        int fn = Sets.difference(expectedPairs, resultPairs).size();
        int tn = Sets.difference(allPairs, intersection).size();
        return new Metrics(tp, tn, fp, fn);
    }
}

