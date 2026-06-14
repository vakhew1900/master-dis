package org.git_tutor.server.config;

import com.google.gson.*;
import org.springframework.boot.autoconfigure.gson.GsonBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class GsonConfig {

    @Bean
    public GsonBuilderCustomizer gsonBuilderCustomizer() {
        return builder -> {
            builder.registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
                    new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
            builder.registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) ->
                    LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            builder.registerTypeAdapter(java.time.LocalDate.class, (JsonSerializer<java.time.LocalDate>) (src, typeOfSrc, context) ->
                    new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE)));
            builder.registerTypeAdapter(java.time.LocalDate.class, (JsonDeserializer<java.time.LocalDate>) (json, typeOfT, context) ->
                    java.time.LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE));

            builder.registerTypeAdapter(java.time.LocalTime.class, (JsonSerializer<java.time.LocalTime>) (src, typeOfSrc, context) ->
                    new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_TIME)));
            builder.registerTypeAdapter(java.time.LocalTime.class, (JsonDeserializer<java.time.LocalTime>) (json, typeOfT, context) ->
                    java.time.LocalTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_TIME));
        };
    }
}
