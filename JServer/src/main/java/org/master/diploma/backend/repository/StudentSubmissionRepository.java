package org.master.diploma.backend.repository;

import org.master.diploma.backend.entity.StudentSubmission;
import org.master.diploma.backend.entity.Task;
import org.master.diploma.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StudentSubmissionRepository extends JpaRepository<StudentSubmission, Long> {
    Optional<StudentSubmission> findByStudentAndTask(User student, Task task);
    List<StudentSubmission> findByStudent(User student);
}
