package org.master.diploma.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = Task.TABLE_NAME)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {
    public static final String TABLE_NAME = "tasks";

    public static class COLUMN_NAMES {
        public static final String ID = "id";
        public static final String NUMBER = "number";
        public static final String DESCRIPTION = "description";
        public static final String REFERENCE_REPO_PATH = "reference_repo_path";
        public static final String LAB_ID = "lab_id";
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_NAMES.ID)
    private Long id;

    @Column(name = COLUMN_NAMES.NUMBER, nullable = false)
    private Integer number;

    @Column(name = COLUMN_NAMES.DESCRIPTION, columnDefinition = "TEXT")
    private String description;

    @Column(name = COLUMN_NAMES.REFERENCE_REPO_PATH, nullable = false)
    private String referenceRepoPath; // Path in MinIO

    @ManyToOne
    @JoinColumn(name = COLUMN_NAMES.LAB_ID, nullable = false)
    private LaboratoryWork lab;
}
