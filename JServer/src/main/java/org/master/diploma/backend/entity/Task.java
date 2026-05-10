package org.master.diploma.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer number;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String referenceRepoPath; // Path in MinIO

    @ManyToOne
    @JoinColumn(name = "lab_id", nullable = false)
    private LaboratoryWork lab;
}
