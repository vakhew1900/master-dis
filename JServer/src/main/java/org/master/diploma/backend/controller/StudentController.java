package org.master.diploma.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.master.diploma.backend.config.Constants;
import org.master.diploma.backend.entity.*;
import org.master.diploma.backend.repository.*;
import org.master.diploma.backend.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(Constants.Routes.STUDENT)
@RequiredArgsConstructor
@Tag(name = "Student Operations", description = "Endpoints for students to view tasks and submit work")
public class StudentController {
    private final TaskRepository taskRepository;
    private final StudentSubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final MinioService minioService;
    private final ComparisonService comparisonService;

    @GetMapping(Constants.Routes.STUDENT_TASKS)
    @Operation(summary = "Get all available tasks")
    public List<Task> getTasks() {
        return taskRepository.findAll();
    }

    @PostMapping(Constants.Routes.STUDENT_UPLOAD)
    @Operation(summary = "Upload task solution (ZIP)")
    public ResponseEntity<StudentSubmission> uploadSolution(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User student = userRepository.findByUsername(username).orElseThrow();
        Task task = taskRepository.findById(id).orElseThrow();

        try {
            String path = minioService.uploadFile("submissions", 
                username + "/" + id + "_" + System.currentTimeMillis() + ".zip", 
                file.getInputStream(), file.getSize(), file.getContentType());

            StudentSubmission submission = submissionRepository.findByStudentAndTask(student, task)
                    .orElse(new StudentSubmission());
            
            submission.setStudent(student);
            submission.setTask(task);
            submission.setStudentRepoPath(path);
            submission.setSubmittedAt(LocalDateTime.now());

            return ResponseEntity.ok(submissionRepository.save(submission));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping(Constants.Routes.STUDENT_CHECK)
    @Operation(summary = "Check own solution")
    public ResponseEntity<String> checkSolution(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User student = userRepository.findByUsername(username).orElseThrow();
        Task task = taskRepository.findById(id).orElseThrow();

        StudentSubmission submission = submissionRepository.findByStudentAndTask(student, task)
                .orElseThrow(() -> new RuntimeException("No submission found"));

        String result = comparisonService.compareRepositories(task.getReferenceRepoPath(), submission.getStudentRepoPath());
        return ResponseEntity.ok(result);
    }
}
