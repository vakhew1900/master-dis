package org.master.diploma.git.graph.subgraphmethod.llm;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import org.master.diploma.git.graph.Graph;
import org.master.diploma.git.graph.GraphCompareResult;
import org.master.diploma.git.graph.JsonPairGraph;
import org.master.diploma.git.graph.label.LabelVertex;
import org.master.diploma.git.graph.subgraphmethod.SubgraphMethodExecutor;
import org.master.diploma.git.json.JsonGraph;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import org.master.diploma.git.label.Label;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.master.diploma.git.graph.GraphCompareResult.LabelError.createLabelError;

public class LLMMethodExecutor extends SubgraphMethodExecutor {

    private static final Gson GSON = new Gson();
    private final LlmConfig cfg;
    private final OpenAIClient client;

    public LLMMethodExecutor() {
        cfg = LlmConfigLoader.load();
        client = OpenAIOkHttpClient.builder()
                .apiKey(cfg.apiKey())
                .baseUrl(cfg.baseUrl()) // можно добавить baseUrl в LlmConfig
                .build();
    }

    @Override
    public <T extends LabelVertex<?>> GraphCompareResult execute(Graph<T> first, Graph<T> second) {

        JsonGraph firstJson = new JsonGraph(first);
        JsonGraph secondJson = new JsonGraph(second);

        String jsonPairGraph = GSON.toJson(new JsonPairGraph(firstJson, secondJson));

        String prompt = String.format(cfg.promt() + "%s", jsonPairGraph);

        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(cfg.model())
                .addUserMessage(prompt)
                .build();

        try {
            ChatCompletion completion = client.chat().completions().create(params);
            String rawText = completion.choices().get(0).message().content().get();


            GraphCompareResult graphCompareResult = GSON.fromJson(rawText, GraphCompareResult.class);
            graphCompareResult.getLabelErrors().clear();
            ;

            graphCompareResult.getMatchingVertices().forEach((key, value) -> {
                graphCompareResult.getLabelErrors()
                        .put(key, createLabelError(first.getVertex(key), second.getVertex(value)));
            });


            graphCompareResult.fillLFinaLabelError(first, second);
            return graphCompareResult;

        } catch (Exception e) {
            throw new RuntimeException("LLM execution failed", e);
        }
    }

    private static String extractPureJson(String text) {
        text = text.trim();
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start == -1 || end == -1 || start > end) {
            throw new IllegalStateException("LLM did not return valid JSON:\n" + text);
        }
        return text.substring(start, end + 1);
    }
}
