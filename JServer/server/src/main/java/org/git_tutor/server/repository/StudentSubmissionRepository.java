package org.git_tutor.server.repository;

import org.git_tutor.server.entity.StudentSubmission;
import org.git_tutor.server.entity.Task;
import org.git_tutor.server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StudentSubmissionRepository extends JpaRepository<StudentSubmission, Long> {
    Optional<StudentSubmission> findByStudentAndTask(User student, Task task);
    List<StudentSubmission> findByStudent(User student);
}
