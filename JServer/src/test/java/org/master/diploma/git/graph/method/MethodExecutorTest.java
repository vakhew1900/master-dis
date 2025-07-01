package org.master.diploma.git.graph.method;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.master.diploma.git.graph.Graph;
import org.master.diploma.git.graph.GraphCompareResult;
import org.master.diploma.git.graph.label.SimpleLabelVertex;
import org.master.diploma.git.graph.subgraphmethod.SubgraphMethodExecutor;
import org.master.diploma.git.json.JsonGraph;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class MethodExecutorTest {

    private static final Gson GSON = new Gson();

    abstract protected SubgraphMethodExecutor getSubgraphMethodExecutor();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class JsonPairGraph {

        public static final String FIRST = "first";
        public static final String SECOND = "second";

        @SerializedName(FIRST)
        JsonGraph first;
        @SerializedName(SECOND)
        JsonGraph second;
    }

    private abstract class NestedTest {

        protected abstract String graphPath(String path);

        protected abstract String resultPath(String path);

        protected void compareGraphTest(String path) throws IOException {
            GraphCompareResult expectedGraphCompared = readGraphCompareResult(resultPath(path));
            JsonPairGraph jsonPairGraph = readGraph(graphPath(path));
            Graph<SimpleLabelVertex> first = jsonPairGraph.getFirst().toGraph();
            Graph<SimpleLabelVertex> second = jsonPairGraph.getSecond().toGraph();
            GraphCompareResult result = getSubgraphMethodExecutor().execute(first, second);
            Assertions.assertEquals(expectedGraphCompared, result);
        }

        private JsonPairGraph readGraph(String path) throws IOException {

            String json = readJson(path);
            return GSON.fromJson(json, JsonPairGraph.class);
        }

        private GraphCompareResult readGraphCompareResult(String path) throws IOException {
            return GSON.fromJson(readJson(path), GraphCompareResult.class);
        }

        private String readJson(String path) throws IOException {
            return String.join(
                    "",
                    Files.readAllLines(Path.of(getClass().getResource(path).getPath()))
            );
        }
    }

    @Nested
    public class BasicTest extends NestedTest {


        @Test
        public void equalGraphTest() throws IOException {
            compareGraphTest("equal.json");
        }

        @Test
        public void deleteListTest() throws IOException {
            compareGraphTest("delete_list.json");
        }

        @Test
        public void notCommonLabelTest() throws IOException {
            compareGraphTest("not_common_labels.json");
        }

        @Test
        public void deleteTransientTest() throws IOException {
            compareGraphTest("delete_transient.json");
        }

        @Override
        protected String graphPath(String path) {
            return "/graph/basic/graph/" + path;
        }

        @Override
        protected String resultPath(String path) {
            return "/graph/basic/result/" + path;
        }
    }

    @Nested
    public class MediumTest extends NestedTest {

        @Test
        public void equalGraphTest() throws IOException {
            compareGraphTest("equal.json");
        }


        @Test
        public void oneLeafGraphTest() throws IOException {
            compareGraphTest("one_leaf.json");
        }

        @Test
        public void severalLeafGraphTest() throws IOException {
            compareGraphTest("several_leaf.json");
        }

        @Test
        public void removeTransitiveVertexTest() throws IOException {
            compareGraphTest("remove_transitive.json");
        }

        @Test
        public void removeTransitiveVertexTest2() throws IOException {
            compareGraphTest("remove_transitive2.json");
        }

        @Test
        public void removeTransitiveVertexTest3() throws IOException {
            compareGraphTest("remove_transitive3.json");
        }

        @Test
        public void removeSeveralTransitiveVertex() throws IOException {
            compareGraphTest("remove_several_transitive.json");
        }

        @Override
        protected String graphPath(String path) {
            return "/graph/medium/graph/" + path;
        }

        @Override
        protected String resultPath(String path) {
            return "/graph/medium/result/" + path;
        }
    }

    @Nested
    public class OneVertexTest extends NestedTest {

        @Test
        public void equalGraphTest() throws IOException {
            compareGraphTest("equal.json");
        }

        @Test
        public void emptyMatchingGraphTest() throws IOException {
            compareGraphTest("empty_matching.json");
        }

        @Test
        public void multiLabelEqualTest() throws IOException {
            compareGraphTest("multi_label_equal.json");
        }

        @Test
        public void multiLabelTest() throws IOException {
            compareGraphTest("multi_label.json");
        }

        @Test
        public void emptyLabelTest() throws IOException {
            compareGraphTest("empty_label.json");
        }

        @Test
        public void differentVertexId() throws IOException {
            compareGraphTest("different_vertex_id.json");
        }


        @Override
        protected String graphPath(String path) {
            return "/graph/one_vertex/graph/" + path;
        }

        @Override
        protected String resultPath(String path) {
            return "/graph/one_vertex/result/" + path;
        }

    }

    @Nested
    public class BigGraphTest extends NestedTest {

        @Test
        public void equalGraphTest() throws IOException {
            compareGraphTest("equal.json");
        }

        @Test
        public void removeLeafsTest() throws IOException {
            compareGraphTest("remove_leaf.json");
        }

        @Test
        public void removeTransientTest() throws IOException {
            compareGraphTest("remove_transient.json");
        }

        @Test
        public void removeTransient2Test() throws IOException {
            compareGraphTest("remove_transient2.json");
        }

        @Test
        public void addBranchTest() throws IOException {
            compareGraphTest("add_branch.json");
        }

        @Test
        public void addBranchTest2() throws IOException {
            compareGraphTest("add_branch2.json");
        }

        @Test
        public void removeBranchTest() throws IOException {
            compareGraphTest("remove_branch.json");
        }

        @Test
        public void differentStructureTest() throws IOException {
            compareGraphTest("different_structure.json");
        }

        @Test
        public void moveBranchTest() throws IOException {
            compareGraphTest("move_branch.json");
        }


        @Override
        protected String graphPath(String path) {
            return "/graph/big/graph/" + path;
        }

        @Override
        protected String resultPath(String path) {
            return "/graph/big/result/" + path;
        }
    }

    @Nested
    public class BambooTest extends NestedTest {

        @Test
        public void equalGraphTest() throws IOException {
            compareGraphTest("equal.json");
        }

        @Test
        public void missingLabelTest() throws IOException {
            compareGraphTest("missing_label.json");
        }

        @Test
        public void missingVertexTest() throws IOException {
            compareGraphTest("missing_vertex.json");
        }

        @Test
        public void extraLabelTest() throws IOException {
            compareGraphTest("extra_label.json");
        }

        @Test
        public void extraVertexTest() throws IOException {
            compareGraphTest("extra_vertex.json");
        }

        @Override
        protected String graphPath(String path) {
            return "/graph/bamboo/graph/" + path;
        }

        @Override
        protected String resultPath(String path) {
            return "/graph/bamboo/result/" + path;
        }
    }
}