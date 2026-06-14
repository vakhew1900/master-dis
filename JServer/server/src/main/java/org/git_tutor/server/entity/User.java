package org.git_tutor.server.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = User.TABLE_NAME)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    public static final String TABLE_NAME = "users";

    public static class COLUMN_NAMES {
        public static final String ID = "id";
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
        public static final String ROLE = "role";
        public static final String FIRST_NAME = "first_name";
        public static final String LAST_NAME = "last_name";
        public static final String MIDDLE_NAME = "middle_name";
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_NAMES.ID)
    private Long id;

    @Column(name = COLUMN_NAMES.USERNAME, unique = true, nullable = false)
    private String username;

    @Column(name = COLUMN_NAMES.PASSWORD, nullable = false)
    private String password;

    @Column(name = COLUMN_NAMES.FIRST_NAME)
    private String firstName;

    @Column(name = COLUMN_NAMES.LAST_NAME)
    private String lastName;

    @Column(name = COLUMN_NAMES.MIDDLE_NAME)
    private String middleName;

    @Enumerated(EnumType.STRING)
    @Column(name = COLUMN_NAMES.ROLE, nullable = false)
    private Role role;

    public enum Role {
        ADMIN, STUDENT
    }
}
