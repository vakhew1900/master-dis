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

/**
 * Implements a subgraph comparison method that leverages a Large Language Model (LLM)
 * to find the Maximum Common Transitive Subgraph (MCTS). This executor serializes the input graphs
 * into JSON, sends them as a prompt to a configured LLM, and then parses the LLM's JSON response
 * to determine the graph comparison result.
 * <p>
 * Реализует метод сравнения подграфов, который использует Большую Языковую Модель (LLM)
 * для поиска Максимального Общего Транзитивного Подграфа (MCTS). Этот исполнитель сериализует входные графы
 * в JSON, отправляет их в качестве промпта сконфигурированной LLM, а затем анализирует JSON-ответ LLM
 * для определения результата сравнения графов.
 */
public class LLMMethodExecutor extends SubgraphMethodExecutor {

    /** Gson instance for JSON serialization/deserialization. <p> Экземпляр Gson для сериализации/десериализации JSON. */
    private static final Gson GSON = new Gson();
    /** Configuration for the Large Language Model. <p> Конфигурация для Большой Языковой Модели. */
    private final LlmConfig cfg;
    /** OpenAI client for interacting with the LLM. <p> Клиент OpenAI для взаимодействия с LLM. */
    private final OpenAIClient client;

    /**
     * Constructs an LLMMethodExecutor, loading configuration and initializing the OpenAI client.
     * <p>
     * Создает LLMMethodExecutor, загружая конфигурацию и инициализируя клиент OpenAI.
     */
    public LLMMethodExecutor() {
        // Load LLM configuration from resources
        // Загружаем конфигурацию LLM из ресурсов
        cfg = LlmConfigLoader.load();
        // Initialize OpenAI client with API key and base URL from configuration
        // Инициализируем клиент OpenAI с ключом API и базовым URL из конфигурации
        client = OpenAIOkHttpClient.builder()
                .apiKey(cfg.apiKey())
                .baseUrl(cfg.baseUrl()) // baseUrl can also be added to LlmConfig / baseUrl также может быть добавлен в LlmConfig
                .build();
    }

    /**
     * Executes the LLM-based subgraph comparison method.
     * It serializes the input graphs to JSON, constructs a prompt for the LLM,
     * sends the request, parses the LLM's response, and processes the result.
     * <p>
     * Выполняет метод сравнения подграфов на основе LLM.
     * Он сериализует входные графы в JSON, формирует промпт для LLM,
     * отправляет запрос, анализирует ответ LLM и обрабатывает результат.
     *
     * @param first  The first graph to compare. / Первый граф для сравнения.
     * @param second The second graph to compare. / Второй граф для сравнения.
     * @param <T>    The type of LabelVertex used in the graphs. / Тип LabelVertex, используемый в графах.
     * @return A GraphCompareResult object containing the matching vertices and label errors. / Объект GraphCompareResult, содержащий сопоставленные вершины и ошибки меток.
     * @throws RuntimeException If the LLM execution fails or returns an invalid response. / Если выполнение LLM завершается с ошибкой или возвращает неверный ответ.
     */
    @Override
    public <T extends LabelVertex<?>> GraphCompareResult execute(Graph<T> first, Graph<T> second) {

        // Convert input graphs to JSON representation
        // Преобразуем входные графы в JSON-представление
        JsonGraph firstJson = new JsonGraph(first);
        JsonGraph secondJson = new JsonGraph(second);

        // Combine into a pair graph JSON string
        // Объединяем в JSON-строку парного графа
        String jsonPairGraph = GSON.toJson(new JsonPairGraph(firstJson, secondJson));

        // Construct the prompt for the LLM using the configured template and the JSON graphs
        // Формируем промпт для LLM, используя настроенный шаблон и JSON-графы
        String prompt = String.format(cfg.promt() + "%s", jsonPairGraph);

        // Build the chat completion request parameters
        // Строим параметры запроса на завершение чата
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(cfg.model())
                .addUserMessage(prompt)
                .build();

        try {
            // Send the request to the OpenAI API and get the completion
            // Отправляем запрос к API OpenAI и получаем завершение
            ChatCompletion completion = client.chat().completions().create(params);
            // Extract the raw text content from the LLM's response
            // Извлекаем необработанный текстовый контент из ответа LLM
            String rawText = completion.choices().get(0).message().content().get();

            // Parse the raw JSON text from the LLM into a GraphCompareResult object
            // Анализируем необработанный JSON-текст от LLM в объект GraphCompareResult
            GraphCompareResult graphCompareResult = GSON.fromJson(rawText, GraphCompareResult.class);
            // Clear any label errors initially provided by the LLM (they will be re-calculated for accuracy)
            // Очищаем любые ошибки меток, первоначально предоставленные LLM (они будут пересчитаны для точности)
            graphCompareResult.getLabelErrors().clear();
            
            // Re-calculate and fill label errors based on the actual graph vertices
            // Пересчитываем и заполняем ошибки меток на основе фактических вершин графа
            graphCompareResult.getMatchingVertices().forEach((key, value) -> {
                graphCompareResult.getLabelErrors()
                        .put(key, createLabelError(first.getVertex(key), second.getVertex(value)));
            });

            // Finalize label errors and apply inversion if necessary
            // Завершаем заполнение ошибок меток и применяем инверсию при необходимости
            graphCompareResult.fillLFinaLabelError(first, second);
            return graphCompareResult;

        } catch (Exception e) {
            // Wrap any exceptions in a RuntimeException for consistent error handling
            // Оборачиваем любые исключения в RuntimeException для единообразной обработки ошибок
            throw new RuntimeException("LLM execution failed", e);
        }
    }

    /**
     * Extracts a pure JSON string from a potentially larger text block.
     * This is useful when an LLM's response might contain conversational text
     * around the actual JSON output.
     * <p>
     * Извлекает чистую JSON-строку из потенциально большего текстового блока.
     * Это полезно, когда ответ LLM может содержать разговорный текст
     * вокруг фактического вывода JSON.
     *
     * @param text The input text containing the JSON. / Входной текст, содержащий JSON.
     * @return The extracted JSON string. / Извлеченная JSON-строка.
     * @throws IllegalStateException If valid JSON cannot be extracted. / Если допустимый JSON не может быть извлечен.
     */
    private static String extractPureJson(String text) {
        text = text.trim(); // Trim whitespace from the text
        // Обрезаем пробелы из текста
        int start = text.indexOf('{'); // Find the start of the JSON object
        // Находим начало JSON-объекта
        int end = text.lastIndexOf('}'); // Find the end of the JSON object
        // Находим конец JSON-объекта
        if (start == -1 || end == -1 || start > end) { // Validate JSON structure
            // Проверяем структуру JSON
            throw new IllegalStateException("LLM did not return valid JSON:\n" + text);
        }
        return text.substring(start, end + 1); // Extract and return the JSON substring
        // Извлекаем и возвращаем JSON-подстроку
    }
}
