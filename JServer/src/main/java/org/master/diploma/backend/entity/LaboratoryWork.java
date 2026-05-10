package org.master.diploma.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "laboratory_works")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaboratoryWork {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer number;

    @Column(nullable = false)
    private String topic;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "lab", cascade = CascadeType.ALL)
    private List<Task> tasks;
}
