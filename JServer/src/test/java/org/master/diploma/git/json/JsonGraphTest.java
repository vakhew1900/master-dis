package org.master.diploma.git.json;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.master.diploma.git.graph.Graph;
import org.master.diploma.git.graph.label.SimpleLabelVertex;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonGraphTest {

    private static Gson GSON = new Gson();

    @Test
    public void typeTest() throws IOException, URISyntaxException {
        readGraph("/json/typeTest.json");
    }

    private Graph<SimpleLabelVertex> readGraph(String path) throws IOException, URISyntaxException {
        String json = String.join("\n", Files.readAllLines(
                        Paths.get(getClass().getResource(path).toURI())
                )
        );
        return GSON.fromJson(json, JsonGraph.class).toGraph();
    }
}
