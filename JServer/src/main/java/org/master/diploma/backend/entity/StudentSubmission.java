package org.master.diploma.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = StudentSubmission.TABLE_NAME)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentSubmission {
    public static final String TABLE_NAME = "student_submissions";

    public static class COLUMN_NAMES {
        public static final String ID = "id";
        public static final String TASK_ID = "task_id";
        public static final String STUDENT_ID = "student_id";
        public static final String STUDENT_REPO_PATH = "student_repo_path";
        public static final String GRADE = "grade";
        public static final String FEEDBACK = "feedback";
        public static final String SUBMITTED_AT = "submitted_at";
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_NAMES.ID)
    private Long id;

    @ManyToOne
    @JoinColumn(name = COLUMN_NAMES.TASK_ID, nullable = false)
    private Task task;

    @ManyToOne
    @JoinColumn(name = COLUMN_NAMES.STUDENT_ID, nullable = false)
    private User student;

    @Column(name = COLUMN_NAMES.STUDENT_REPO_PATH, nullable = false)
    private String studentRepoPath; // Path in MinIO

    @Column(name = COLUMN_NAMES.GRADE)
    private Double grade;

    @Column(name = COLUMN_NAMES.FEEDBACK, columnDefinition = "TEXT")
    private String feedback;

    @Column(name = COLUMN_NAMES.SUBMITTED_AT, nullable = false)
    private LocalDateTime submittedAt;
}
