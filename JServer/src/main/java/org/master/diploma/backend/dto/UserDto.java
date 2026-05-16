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
        public static final String FIRST_NAME = "firstName";
        public static final String LAST_NAME = "lastName";
        public static final String MIDDLE_NAME = "middleName";
        public static final String PASSWORD = "password";
    }

    @SerializedName(FIELDS.ID)
    private Long id;

    @SerializedName(FIELDS.USERNAME)
    private String username;

    @SerializedName(FIELDS.ROLE)
    private String role;

    @SerializedName(FIELDS.FIRST_NAME)
    private String firstName;

    @SerializedName(FIELDS.LAST_NAME)
    private String lastName;

    @SerializedName(FIELDS.MIDDLE_NAME)
    private String middleName;

    @SerializedName(FIELDS.PASSWORD)
    private String password;
}
