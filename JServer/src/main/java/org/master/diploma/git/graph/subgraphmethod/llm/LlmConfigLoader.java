package org.master.diploma.git.graph.subgraphmethod.llm;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.aeonbits.owner.ConfigFactory;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Properties;

/**
 * Utility class for loading Large Language Model (LLM) configuration from a JSON file.
 * It reads `llm.json` from the resources, parses it into a map, and then converts
 * these properties into an `LlmConfig` interface using the OWNER library.
 * <p>
 * Вспомогательный класс для загрузки конфигурации Большой Языковой Модели (LLM) из JSON-файла.
 * Он считывает `llm.json` из ресурсов, разбирает его в карту, а затем преобразует
 * эти свойства в интерфейс `LlmConfig` с использованием библиотеки OWNER.
 */
public final class LlmConfigLoader {

    /** Gson instance for parsing JSON. <p> Экземпляр Gson для парсинга JSON. */
    private static final Gson GSON = new Gson();

    /** Private constructor to prevent instantiation of this utility class. <p> Приватный конструктор для предотвращения создания экземпляров этого утилитного класса. */
    private LlmConfigLoader() {}

    /**
     * Loads the LLM configuration from the `llm.json` resource file.
     * The JSON file is parsed into a Map, then converted into Java Properties,
     * and finally used to create an instance of the `LlmConfig` interface.
     * <p>
     * Загружает конфигурацию LLM из файла ресурсов `llm.json`.
     * JSON-файл разбирается в Map, затем преобразуется в Java Properties,
     * и, наконец, используется для создания экземпляра интерфейса `LlmConfig`.
     *
     * @return An instance of LlmConfig containing the loaded settings. / Экземпляр LlmConfig, содержащий загруженные настройки.
     * @throws RuntimeException If the `llm.json` file cannot be found or loaded. / Если файл `llm.json` не найден или не может быть загружен.
     */
    public static LlmConfig load() {
        try (InputStream is =
                     LlmConfigLoader.class.getResourceAsStream("/llm.json")) { // Attempt to read llm.json from resources
            // Пытаемся прочитать llm.json из ресурсов

            if (is == null) { // If the resource is not found
                // Если ресурс не найден
                throw new IllegalStateException("llm.json not found in resources");
            }

            // Define the type for Gson to parse the JSON into a Map
            // Определяем тип для Gson для парсинга JSON в Map
            Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
            // Parse the JSON input stream into a Map
            // Разбираем входной поток JSON в Map
            Map<String, Object> json = GSON.fromJson(
                    new java.io.InputStreamReader(is),
                    mapType
            );

            // Convert the Map to Java Properties
            // Преобразуем Map в Java Properties
            Properties props = new Properties();
            json.forEach((k, v) -> props.put(k, String.valueOf(v)));

            // Create and return an LlmConfig instance using the loaded properties
            // Создаем и возвращаем экземпляр LlmConfig, используя загруженные свойства
            return ConfigFactory.create(
                    LlmConfig.class,
                    props
            );

        } catch (Exception e) { // Catch any exception during loading and wrap it in a RuntimeException
            // Перехватываем любое исключение во время загрузки и оборачиваем его в RuntimeException
            throw new RuntimeException("Failed to load llm.json", e);
        }
    }
}
