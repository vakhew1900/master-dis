package org.git_tutor.server.repository;

import org.git_tutor.server.entity.LaboratoryWork;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LaboratoryWorkRepository extends JpaRepository<LaboratoryWork, Long> {
}
