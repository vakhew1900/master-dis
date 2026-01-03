package org.master.diploma.git.generator;

import org.master.diploma.git.graph.Graph;
import org.master.diploma.git.graph.GraphCompareResult;
import org.master.diploma.git.graph.Vertex;
import org.master.diploma.git.graph.label.LabelVertex;
import org.master.diploma.git.label.SimpleLabel;

import static org.master.diploma.git.generator.TreeGenerator.MAX_LABEL_NUMBER;
import static org.master.diploma.git.generator.TreeGenerator.generateVertex;

public class DiffGenerator {

    public static <T extends Graph<LabelVertex<SimpleLabel>>> void diff(GraphGeneratorEntity<T> graphGeneratorEntity) {
        DiffType diffType =  DiffType.getByNumber(RandomUtils.nextInt(0,  DiffType.DIFF_TYPE_SIZE));

        switch (diffType) {
            case REMOVE_LABEL -> {
                removeLabel(graphGeneratorEntity);
            }
            case ADD_LABEL -> {
                addLabel(graphGeneratorEntity);
            }
            case MOVE_LABEL -> {
                moveLabel(graphGeneratorEntity);
            }
            case REMOVE_VERTEX -> {
                removeVertex(graphGeneratorEntity);
            }
            case ADD_VERTEX -> {
                addVertex(graphGeneratorEntity);
            }
            case MOVE_VERTEX -> {
              //  moveVertex(graphGeneratorEntity);
            }
        };

    }

    private static <T extends Graph<LabelVertex<SimpleLabel>>> void moveLabel(GraphGeneratorEntity<T> graphGeneratorEntity) {
        int vertexNumber = randomVertex(graphGeneratorEntity);
        if (graphGeneratorEntity.getSecond().getVertices().get(vertexNumber).getLabels().size() > 1) {
            SimpleLabel label = removeLabel(graphGeneratorEntity, vertexNumber);
            addLabel(graphGeneratorEntity, label);
        }
    }



    private static <T extends Graph<LabelVertex<SimpleLabel>>> void addVertex(GraphGeneratorEntity<T> graphGeneratorEntity) {
        int parentVertexNumber = randomVertex(graphGeneratorEntity);
        int curVertexNumber = graphGeneratorEntity
                .getSecond()
                .getVertices()
                .stream()
                .mapToInt(Vertex::getNumber)
                .max()
                .getAsInt() + 1;

        var vertex = generateVertex(curVertexNumber, 1);
        graphGeneratorEntity.getSecond().addVertex(vertex);
        graphGeneratorEntity.getSecond().addEdge(
                graphGeneratorEntity.getSecond().getVertices().get(parentVertexNumber).getNumber(),
                curVertexNumber
        );
    }

    private static <T extends Graph<LabelVertex<SimpleLabel>>> void removeVertex(GraphGeneratorEntity<T> graphGeneratorEntity) {
        int vertexNumber = randomVertex(graphGeneratorEntity, 1);

        var vertex = graphGeneratorEntity.getSecond().getVertices().get(vertexNumber) ;
        var firstVertex = graphGeneratorEntity.getFirst().getVertex(vertex.getNumber());
        boolean updateCompareResult = firstVertex != null;

        graphGeneratorEntity.getSecond().removeVertex(vertex);

        if (updateCompareResult) {
            graphGeneratorEntity
                    .getGraphCompareResult()
                    .getMatchingVertices()
                    .remove(firstVertex.getNumber());
        }
    }

    private static <T extends Graph<LabelVertex<SimpleLabel>>> void addLabel(GraphGeneratorEntity<T> graphGeneratorEntity) {
        SimpleLabel label = new SimpleLabel( RandomUtils.nextInt(1, MAX_LABEL_NUMBER));
        addLabel(graphGeneratorEntity, label);
    }

    private static <T extends Graph<LabelVertex<SimpleLabel>>> void addLabel(GraphGeneratorEntity<T> graphGeneratorEntity, SimpleLabel label) {
        int vertexNumber = randomVertex(graphGeneratorEntity);
        LabelVertex<SimpleLabel> vertex = graphGeneratorEntity.getSecond().getVertices().get(vertexNumber);
        vertex.addLabel(label);

        var labelError = getLabelError(graphGeneratorEntity, vertex.getNumber());
        if (labelError.getExtraLabels().contains(label.getId())) {
            labelError.getExtraLabels().remove((Integer) label.getId());
        } else {
            labelError.getMissingLabels()
                    .add(label.getId());
        }

    }

    private static <T extends Graph<LabelVertex<SimpleLabel>>> void removeLabel(GraphGeneratorEntity<T> graphGeneratorEntity) {
        int vertexNumber = randomVertex(graphGeneratorEntity);
        if (graphGeneratorEntity.getSecond().getVertices().get(vertexNumber).getLabels().size() > 1) {
            removeLabel(graphGeneratorEntity, vertexNumber);
        }
    }

    private static <T extends Graph<LabelVertex<SimpleLabel>>> SimpleLabel removeLabel(GraphGeneratorEntity<T> graphGeneratorEntity,  int vertexNumber) {
        int labelNumber = randomLabel(graphGeneratorEntity, vertexNumber);
        LabelVertex<SimpleLabel> vertex = graphGeneratorEntity.getSecond().getVertices().get(vertexNumber);
        SimpleLabel label = vertex.getLabels().get(labelNumber);
        LabelVertex<?> firstVertex = graphGeneratorEntity.getFirst().getVertex(vertex.getNumber());
        boolean updateCompareResult = false;
        if (firstVertex != null) {

            for (var element :  firstVertex.getLabels()) {
                updateCompareResult = updateCompareResult || element.getId() == label.getId();
            }
        }

        vertex.removeLabel(label.getId());

        if (updateCompareResult) {

            var labelError = getLabelError(graphGeneratorEntity, vertex.getNumber());
            if (labelError.getMissingLabels().contains(label.getId())) {
                labelError.getMissingLabels().remove((Integer) label.getId());
            } else {
                labelError.getExtraLabels()
                        .add(label.getId());
            }
        }

        return  label;
    }

    private static <T extends Graph<LabelVertex<SimpleLabel>>>  GraphCompareResult.LabelError getLabelError(GraphGeneratorEntity<T> entity, int number) {
        var labelErrors = entity.getGraphCompareResult().getLabelErrors();
        if (!labelErrors.containsKey(number)) {
           labelErrors.put(number, new GraphCompareResult.LabelError());
        }
        return  labelErrors.get(number);
    }

    private static  <T extends Graph<LabelVertex<SimpleLabel>>>  int randomVertex(GraphGeneratorEntity<T> graphGeneratorEntity) {
       return randomVertex(graphGeneratorEntity, 0);
    }

    private static  <T extends Graph<LabelVertex<SimpleLabel>>>  int randomVertex(GraphGeneratorEntity<T> graphGeneratorEntity, int left) {
        int verticesSize = graphGeneratorEntity.getSecond().getVertices().size();
        return RandomUtils.nextInt(left, verticesSize - 1);
    }


    private static <T extends Graph<LabelVertex<SimpleLabel>>> int randomLabel(GraphGeneratorEntity<T> graphGeneratorEntity, int vertexNumber) {
        int labelCount = graphGeneratorEntity.getSecond().getVertices().get(vertexNumber).getLabels().size();
        return RandomUtils.nextInt(0, labelCount - 1);
    }

    private enum DiffType {
        REMOVE_LABEL(0),
        ADD_LABEL(1),
        REMOVE_VERTEX(2),
        ADD_VERTEX(3),
        MOVE_LABEL(4),
        MOVE_VERTEX(5);


        public static final int DIFF_TYPE_SIZE = DiffType.values().length - 1;

        public int getNumber() {
            return number;
        }

        private final int number;

        DiffType(int number) {
            this.number = number;
        }


        public static DiffType getByNumber(int number) {
            for (var value : values()) {
                if (value.getNumber() == number) {
                    return value;
                }
            }

            throw new IllegalArgumentException( String.format("Incorrect diff type {%d}", number));
        }
    }
}
