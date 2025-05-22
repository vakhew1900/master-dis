package org.master.diploma.git.graph;

public class BruteForceMethod {


    public static <T extends Vertex> void execute(
            Graph<T> first,
            Graph<T> second
    ) {
        boolean invert = first.getVertices().size() < second.getVertices().size();

        if (invert) {
            var tmp = first;
            first = second;
            second = tmp;
        }


        Map<Integer, Integer>
        for (T u : first.getVertices()) {
            for (T v : second.getVertices()) {

                if (u)
            }
        }
    }

}
