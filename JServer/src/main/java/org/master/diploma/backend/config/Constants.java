package org.master.diploma.backend.config;

public class Constants {
    public static class Routes {
        public static final String API_PREFIX = "/api";
        
        public static final String AUTH = API_PREFIX + "/auth";
        public static final String AUTH_REGISTER = "/register";
        public static final String AUTH_LOGIN = "/login";

        public static final String ADMIN = API_PREFIX + "/admin";
        public static final String ADMIN_LABS = ADMIN + "/labs";
        public static final String ADMIN_TASKS = ADMIN + "/tasks";
        public static final String ADMIN_SUBMISSIONS = ADMIN + "/submissions";

        public static final String STUDENT = API_PREFIX + "/student";
        public static final String STUDENT_TASKS = STUDENT + "/tasks";
        public static final String STUDENT_UPLOAD = "/{id}/upload";
        public static final String STUDENT_CHECK = "/{id}/check";
    }

    public static class Buckets {
        public static final String REFERENCES = "references";
        public static final String SUBMISSIONS = "submissions";
    }
}
