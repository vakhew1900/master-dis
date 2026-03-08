package org.master.diploma.git.graph.dto.samples;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for author information.
 * <p>
 * Объект передачи данных для информации об авторе.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDto {
    /**
     * Defines constants for the field names used in JSON serialization.
     * <p>
     * Определяет константы для имен полей, используемых при JSON-сериализации.
     */
    public static class FIELDS {
        public static final String NAME = "name";
        public static final String EMAIL = "email";
    }

    /**
     * Author's name.
     * <p>
     * Имя автора.
     */
    @SerializedName(FIELDS.NAME)
    private String name;

    /**
     * Author's email.
     * <p>
     * Email автора.
     */
    @SerializedName(FIELDS.EMAIL)
    private String email;
}
