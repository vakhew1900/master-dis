package org.master.diploma.git.graph.subgraphmethod.llm;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.aeonbits.owner.ConfigFactory;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Properties;

public final class LlmConfigLoader {

    private static final Gson GSON = new Gson();

    private LlmConfigLoader() {}

    public static LlmConfig load() {
        try (InputStream is =
                     LlmConfigLoader.class.getResourceAsStream("/llm.json")) {

            if (is == null) {
                throw new IllegalStateException("llm.json not found in resources");
            }

            Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> json = GSON.fromJson(
                    new java.io.InputStreamReader(is),
                    mapType
            );

            Properties props = new Properties();
            json.forEach((k, v) -> props.put(k, String.valueOf(v)));

            return ConfigFactory.create(
                    LlmConfig.class,
                    props
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to load llm.json", e);
        }
    }
}
