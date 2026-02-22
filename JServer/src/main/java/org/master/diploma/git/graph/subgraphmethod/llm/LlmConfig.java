package org.master.diploma.git.graph.subgraphmethod.llm;

import org.aeonbits.owner.Config;

/**
 * Configuration interface for Large Language Model (LLM) settings.
 * This interface uses the OWNER library to load properties from a configuration source,
 * typically a JSON file (e.g., `llm.json`), providing parameters necessary
 * for interacting with an LLM service.
 * <p>
 * Интерфейс конфигурации для настроек Большой Языковой Модели (LLM).
 * Этот интерфейс использует библиотеку OWNER для загрузки свойств из источника конфигурации,
 * обычно JSON-файла (например, `llm.json`), предоставляя параметры, необходимые
 * для взаимодействия со службой LLM.
 */
public interface LlmConfig extends Config {

    /**
     * Specifies the LLM model to use (e.g., "gpt-4", "gemini-pro").
     * <p>
     * Указывает модель LLM для использования (например, "gpt-4", "gemini-pro").
     * @return The name of the LLM model. / Название модели LLM.
     */
    @Key("model")
    String model();

    /**
     * Provides the API key for authenticating with the LLM service.
     * <p>
     * Предоставляет ключ API для аутентификации в службе LLM.
     * @return The API key. / Ключ API.
     */
    @Key("api-key")
    String apiKey();

    /**
     * Defines the base prompt template to be sent to the LLM.
     * This prompt is typically augmented with serialized graph data.
     * <p>
     * Определяет базовый шаблон промпта, отправляемого в LLM.
     * Этот промпт обычно дополняется сериализованными данными графа.
     * @return The base prompt string. / Базовая строка промпта.
     */
    @Key("promt")
    String promt();
    /**
     * Specifies the base URL for the LLM API endpoint.
     * <p>
     * Указывает базовый URL для конечной точки LLM API.
     * @return The base URL as a string. / Базовый URL в виде строки.
     */
    @Key("baseUrl")
    String baseUrl();
}
