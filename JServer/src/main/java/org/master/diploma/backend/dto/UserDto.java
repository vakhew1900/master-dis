package org.master.diploma.backend.dto;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    public static class FIELDS {
        public static final String ID = "id";
        public static final String USERNAME = "username";
        public static final String ROLE = "role";
    }

    @SerializedName(FIELDS.ID)
    private Long id;

    @SerializedName(FIELDS.USERNAME)
    private String username;

    @SerializedName(FIELDS.ROLE)
    private String role;
}
