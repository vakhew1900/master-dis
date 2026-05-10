package org.master.diploma.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.master.diploma.backend.config.Constants;
import org.master.diploma.backend.entity.StudentSubmission;
import org.master.diploma.backend.entity.Task;
import org.master.diploma.backend.entity.User;
import org.master.diploma.backend.repository.StudentSubmissionRepository;
import org.master.diploma.backend.repository.TaskRepository;
import org.master.diploma.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(Constants.Routes.ADMIN + "/students")
@RequiredArgsConstructor
@Tag(name = "Admin Student Management", description = "Endpoints for teachers to manage students and their assignments")
public class AdminStudentController {
    private final UserRepository userRepository;
    private final StudentSubmissionRepository submissionRepository;
    private final TaskRepository taskRepository;

    @GetMapping
    @Operation(summary = "Get all students")
    public List<User> getAllStudents() {
        return userRepository.findByRole(User.Role.STUDENT);
    }

    @GetMapping("/submissions")
    @Operation(summary = "Get all student submissions")
    public List<StudentSubmission> getAllSubmissions() {
        return submissionRepository.findAll();
    }

    @PostMapping("/{studentId}/assign/{taskId}")
    @Operation(summary = "Assign task to student")
    public ResponseEntity<StudentSubmission> assignTask(@PathVariable Long studentId, @PathVariable Long taskId) {
        User student = userRepository.findById(studentId).orElseThrow();
        Task task = taskRepository.findById(taskId).orElseThrow();

        StudentSubmission submission = submissionRepository.findByStudentAndTask(student, task)
                .orElse(StudentSubmission.builder()
                        .student(student)
                        .task(task)
                        .submittedAt(LocalDateTime.now()) // Placeholder until actual submission
                        .studentRepoPath("NOT_SUBMITTED")
                        .build());
        
        return ResponseEntity.ok(submissionRepository.save(submission));
    }

    @DeleteMapping("/submissions/{submissionId}")
    @Operation(summary = "Remove task assignment/submission")
    public ResponseEntity<Void> removeAssignment(@PathVariable Long submissionId) {
        submissionRepository.deleteById(submissionId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/submissions/{submissionId}/grade")
    @Operation(summary = "Grade student submission")
    public ResponseEntity<StudentSubmission> gradeSubmission(
            @PathVariable Long submissionId, 
            @RequestParam Double grade,
            @RequestParam(required = false) String feedback) {
        
        StudentSubmission submission = submissionRepository.findById(submissionId).orElseThrow();
        submission.setGrade(grade);
        submission.setFeedback(feedback);
        return ResponseEntity.ok(submissionRepository.save(submission));
    }
}
