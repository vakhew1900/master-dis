package org.git_tutor.server.repository;

import org.git_tutor.server.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
