package org.git_tutor.server.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = LaboratoryWork.TABLE_NAME)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaboratoryWork {
    public static final String TABLE_NAME = "laboratory_works";

    public static class COLUMN_NAMES {
        public static final String ID = "id";
        public static final String NUMBER = "number";
        public static final String TOPIC = "topic";
        public static final String DESCRIPTION = "description";
        public static final String MAX_GRADE = "max_grade";
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_NAMES.ID)
    private Long id;

    @Column(name = COLUMN_NAMES.NUMBER, nullable = false)
    private Integer number;

    @Column(name = COLUMN_NAMES.TOPIC, nullable = false)
    private String topic;

    @Column(name = COLUMN_NAMES.DESCRIPTION, columnDefinition = "TEXT")
    private String description;

    @Column(name = COLUMN_NAMES.MAX_GRADE, nullable = false)
    private Double maxGrade;

    @OneToMany(mappedBy = "lab", cascade = CascadeType.ALL)
    private List<Task> tasks;
}
