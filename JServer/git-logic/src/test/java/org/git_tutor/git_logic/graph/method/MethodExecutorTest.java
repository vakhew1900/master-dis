package org.git_tutor.git_logic.graph.method;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.git_tutor.git_logic.graph.Graph;
import org.git_tutor.git_logic.graph.GraphCompareResult;
import org.git_tutor.git_logic.graph.JsonPairGraph;
import org.git_tutor.git_logic.graph.label.SimpleLabelVertex;
import org.git_tutor.git_logic.graph.subgraphmethod.BranchMethodExecutor;
import org.git_tutor.git_logic.graph.subgraphmethod.SubgraphMethodExecutor;
import org.git_tutor.git_logic.metrics.Metrics;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.git_tutor.git_logic.metrics.Metrics.findMetric;

/**
 * Abstract base class for testing different SubgraphMethodExecutor implementations.
 * It provides common testing infrastructure, including loading test graphs from JSON,
 * executing the comparison methods, and asserting the results against expected outcomes.
 * <p>
 * Абстрактный базовый класс для тестирования различных реализаций SubgraphMethodExecutor.
 * Он предоставляет общую тестовую инфраструктуру, включая загрузку тестовых графов из JSON,
 * выполнение методов сравнения и проверку результатов по отношению к ожидаемым.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class MethodExecutorTest {

    private static final Logger LOG = LogManager.getLogger(BranchMethodExecutor.class);
    private static final Gson GSON = new Gson();
    abstract protected SubgraphMethodExecutor getSubgraphMethodExecutor();

    private Metrics metrics = new Metrics(0, 0, 0, 0);

    protected String getMethodExecutorClassName() {
        return this.getClass().getSimpleName();
    }

    @AfterAll
    public void printMetrics() {
        System.out.println(metrics.toCompactTable());
    }


    private abstract class NestedTest {

        protected abstract String graphPath(String path);

        protected abstract String resultPath(String path);

        protected void compareGraphTest(String path) throws IOException, URISyntaxException {
            GraphCompareResult expectedGraphCompared = readGraphCompareResult(resultPath(path));
            JsonPairGraph jsonPairGraph = readGraph(graphPath(path));


            Graph<SimpleLabelVertex> first = jsonPairGraph.getFirst().toGraph();
            Graph<SimpleLabelVertex> second = jsonPairGraph.getSecond().toGraph();
            GraphCompareResult result;

            try {
                result = getSubgraphMethodExecutor().execute(first, second);
            } catch (Exception e) {
                result = new GraphCompareResult();
                LOG.warn(e.toString());
            }

            metrics.add(findMetric(first, second, expectedGraphCompared, result));
            printGraphs(first, second, expectedGraphCompared, result);
            Assertions.assertEquals(expectedGraphCompared, result);

        }

        private void printGraphs
                (Graph<SimpleLabelVertex> first,
                 Graph<SimpleLabelVertex> second,
                 GraphCompareResult expectedGraphCompared,
                 GraphCompareResult result)
        {

            System.out.println("first = " + first.toGraphviz());
            System.out.println("second= " + second.toGraphviz() );
            System.out.println("expected=" + first.getSubGraph(expectedGraphCompared.getMatchingVertices().keySet()).toGraphviz());
            System.out.println("result=" + first.getSubGraph(result.getMatchingVertices().keySet()).toGraphviz());
        }

        private JsonPairGraph readGraph(String path) throws IOException, URISyntaxException {

            String json = readJson(path);
            return GSON.fromJson(json, JsonPairGraph.class);
        }

        private GraphCompareResult readGraphCompareResult(String path) throws IOException, URISyntaxException {
            return GSON.fromJson(readJson(path), GraphCompareResult.class);
        }

        private String readJson(String path) throws IOException, URISyntaxException {
            return String.join(
                    "",
                    Files.readAllLines(Path.of(getClass().getResource(path).toURI()))
            );
        }
    }

    @Nested
    public class BasicTest extends NestedTest {


        @Test
        public void equalGraphTest() throws IOException, URISyntaxException {
            compareGraphTest("equal.json");
        }

        @Test
        public void deleteListTest() throws IOException, URISyntaxException {
            compareGraphTest("delete_list.json");
        }

        @Test
        public void notCommonLabelTest() throws IOException, URISyntaxException {
            compareGraphTest("not_common_labels.json");
        }

        @Test
        public void deleteTransientTest() throws IOException, URISyntaxException {
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
        public void equalGraphTest() throws IOException, URISyntaxException {
            compareGraphTest("equal.json");
        }


        @Test
        public void oneLeafGraphTest() throws IOException, URISyntaxException {
            compareGraphTest("one_leaf.json");
        }

        @Test
        public void severalLeafGraphTest() throws IOException, URISyntaxException {
            compareGraphTest("several_leaf.json");
        }

        @Test
        public void removeTransitiveVertexTest() throws IOException, URISyntaxException {
            compareGraphTest("remove_transitive.json");
        }

        @Test
        public void removeTransitiveVertexTest2() throws IOException, URISyntaxException {
            compareGraphTest("remove_transitive2.json");
        }

        @Test
        public void removeTransitiveVertexTest3() throws IOException, URISyntaxException {
            compareGraphTest("remove_transitive3.json");
        }

        @Test
        public void removeSeveralTransitiveVertex() throws IOException, URISyntaxException {
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
        public void equalGraphTest() throws IOException, URISyntaxException {
            compareGraphTest("equal.json");
        }

        @Test
        public void emptyMatchingGraphTest() throws IOException, URISyntaxException {
            compareGraphTest("empty_matching.json");
        }

        @Test
        public void multiLabelEqualTest() throws IOException, URISyntaxException {
            compareGraphTest("multi_label_equal.json");
        }

        @Test
        public void multiLabelTest() throws IOException, URISyntaxException {
            compareGraphTest("multi_label.json");
        }

        @Test
        public void emptyLabelTest() throws IOException, URISyntaxException {
            compareGraphTest("empty_label.json");
        }

        @Test
        public void differentVertexId() throws IOException, URISyntaxException {
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
        public void equalGraphTest() throws IOException, URISyntaxException {
            compareGraphTest("equal.json");
        }

        @Test
        public void removeLeafsTest() throws IOException, URISyntaxException {
            compareGraphTest("remove_leaf.json");
        }

        @Test
        public void removeTransientTest() throws IOException, URISyntaxException {
            compareGraphTest("remove_transient.json");
        }

        @Test
        public void removeTransient2Test() throws IOException, URISyntaxException {
            compareGraphTest("remove_transient2.json");
        }

        @Test
        public void addBranchTest() throws IOException, URISyntaxException {
            compareGraphTest("add_branch.json");
        }

        @Test
        public void addBranchTest2() throws IOException, URISyntaxException {
            compareGraphTest("add_branch2.json");
        }

        @Test
        public void removeBranchTest() throws IOException, URISyntaxException {
            compareGraphTest("remove_branch.json");
        }

        @Test
        public void differentStructureTest() throws IOException, URISyntaxException {
            compareGraphTest("different_structure.json");
        }

        @Test
        public void moveBranchTest() throws IOException, URISyntaxException {
            compareGraphTest("move_branch.json");
        }

        @Test
        public void moveBranch2Test() throws IOException, URISyntaxException {
            compareGraphTest("move_branch2.json");
        }

        @Test
        public void moveBranchToMiddleTest() throws IOException, URISyntaxException {
            compareGraphTest("move_branch_to_middle.json");
        }

        @Test
        public void complexTest() throws IOException, URISyntaxException {
            compareGraphTest("complex.json");
        }

        @Test
        public void complexTest2() throws IOException, URISyntaxException {
            compareGraphTest("complex2.json");
        }

        @Test
        public void complexTest3() throws IOException, URISyntaxException {
            compareGraphTest("complex3.json");
        }

        @Test
        public void complexTest4() throws IOException, URISyntaxException {
            compareGraphTest("complex4.json");
        }

        @Test
        public void divVertexToSeveralVerticesTest() throws IOException, URISyntaxException {
            compareGraphTest("div_vertex_to_several.json");
        }

        @Test
        public void divVertexToSeveralVerticesTest2() throws IOException, URISyntaxException {
            compareGraphTest("div_vertex_to_several2.json");
        }

        @Test
        public void divVertexToSeveralVerticesTest3() throws IOException, URISyntaxException {
            compareGraphTest("div_vertex_to_several3.json");
        }

        @Test
        public void squashTest() throws IOException, URISyntaxException {
            compareGraphTest("squash.json");
        }

        @Test
        @DisplayName("One vertex is divided into two, but not consecutive ones")
        public void divTransitTest() throws IOException, URISyntaxException {
            compareGraphTest("div_transit.json");
        }

        @Test
        @DisplayName("One vertex is divided into two, but not consecutive ones 2")
        public void divTransit2Test() throws IOException, URISyntaxException {
            compareGraphTest("div_transit2.json");
        }

        @Test
        public void onlyRootMatchTest() throws IOException, URISyntaxException {
            compareGraphTest("only_root_match.json");
        }

        @Test
        public void complex5Test() throws IOException, URISyntaxException {
            compareGraphTest("complex5.json");
        }

        @Test
        public void complex7Test() throws IOException, URISyntaxException {
            compareGraphTest("complex7.json");
        }


        @Test
        public void complex6Test() throws IOException, URISyntaxException {
            compareGraphTest("complex6.json");
        }

        @Test
        public void squashTransitTest() throws IOException, URISyntaxException {
            compareGraphTest("squash_transit.json");
        }

        @Test
        public void moveLabel() throws IOException, URISyntaxException {
            compareGraphTest("move_label.json");
        }

        @Test
        public void moveLabel2() throws IOException, URISyntaxException {
            compareGraphTest("move_label2.json");
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
    public class DAGTest extends NestedTest {

        @Test
        public void equalGraphTest() throws IOException, URISyntaxException {
            compareGraphTest("equal.json");
        }

        @Test
        public void equalGraphTest2() throws IOException, URISyntaxException {
            compareGraphTest("small_equal.json");
        }

        @Test
        public void smallestEqualGraphTest() throws IOException, URISyntaxException {
            compareGraphTest("smallest_equal.json");
        }

        @Test
        public void deleteVertexTest() throws IOException, URISyntaxException {
            compareGraphTest("delete_vertex.json");
        }

        @Test
        public void deleteMergeVertexTest() throws IOException, URISyntaxException {
            compareGraphTest("delete_merge_vertex.json");
        }

        @Test
        public void deleteParentVertexTest() throws IOException, URISyntaxException {
            compareGraphTest("delete_parent_vertex.json");
        }

        @Test
        public void complexDeleteVertexTest() throws IOException, URISyntaxException {
            compareGraphTest("complex_delete_vertex_test.json");
        }

        @Test
        public void squashTest() throws IOException, URISyntaxException {
            compareGraphTest("squash.json"); //TODO DpMEthod тоже дает правильный ответ, немного отличный от текущего
        }

        @Override
        protected String graphPath(String path) {
            return "/graph/dag/graph/" + path;
        }

        @Override
        protected String resultPath(String path) {
            return "/graph/dag/result/" + path;
        }

        @Test
        public void removeBranchTest() throws IOException, URISyntaxException {
            compareGraphTest("remove_branch.json");
        }

    }

    @Nested
    public class AdditionalTest extends  NestedTest {

        @DisplayName("Two branches contains equals set of label")
        @Test
        public void twoBranchEqualLabelSet() throws IOException, URISyntaxException {
            compareGraphTest("two_equal_branch.json");
        }

        @Test
        public void reverseBranchTest() throws IOException, URISyntaxException {
            compareGraphTest("reverse_branch.json");
        }

        @Override
        protected String graphPath(String path) {
            return "/graph/additional/graph/" + path;
        }

        @Override
        protected String resultPath(String path) {
            return "/graph/additional/result/" + path;
        }
    }

    @Nested
    public class BambooTest extends NestedTest {

        @Test
        public void equalGraphTest() throws IOException, URISyntaxException {
            compareGraphTest("equal.json");
        }

        @Test
        public void missingLabelTest() throws IOException, URISyntaxException {
            compareGraphTest("missing_label.json");
        }

        @Test
        public void missingVertexTest() throws IOException, URISyntaxException {
            compareGraphTest("missing_vertex.json");
        }

        @Test
        public void extraLabelTest() throws IOException, URISyntaxException {
            compareGraphTest("extra_label.json");
        }

        @Test
        public void extraVertexTest() throws IOException, URISyntaxException {
            compareGraphTest("extra_vertex.json");
        }

        @Test
        public void moveVertexTest() throws IOException, URISyntaxException {
            compareGraphTest("move_vertex.json");
        }

        @Test
        public void moveVertex2Test() throws IOException, URISyntaxException {
            compareGraphTest("move_vertex2.json");
        }

        @Test
        public void moveVertex3Test() throws IOException, URISyntaxException {
            compareGraphTest("move_vertex3.json");
        }

        @Test
        public void severalMoveVertexTest() throws IOException, URISyntaxException {
            compareGraphTest("several_move_vertex.json");
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