package org.master.diploma.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.master.diploma.backend.config.Constants;
import org.master.diploma.backend.entity.*;
import org.master.diploma.backend.repository.*;
import org.master.diploma.backend.service.*;
import org.master.diploma.backend.support.FileHelper;
import org.master.diploma.git.graph.dto.GitComparisonResultDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(Constants.Routes.STUDENT)
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Student Operations", description = "Endpoints for students to view tasks and submit work")
public class StudentController {
    private final TaskRepository taskRepository;
    private final StudentSubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final FileService fileService;
    private final ComparisonService comparisonService;

    @GetMapping(Constants.Routes.STUDENT_TASKS)
    @Operation(summary = "Get all available tasks")
    public List<Task> getTasks() {
        return taskRepository.findAll();
    }

    @PostMapping(Constants.Routes.STUDENT_UPLOAD)
    @Operation(summary = "Upload task solution (ZIP)")
    public ResponseEntity<StudentSubmission> uploadSolution(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User student = userRepository.findByUsername(username).orElseThrow();
        Task task = taskRepository.findById(id).orElseThrow();

        StudentSubmission submission = submissionRepository.findByStudentAndTask(student, task)
                .orElse(StudentSubmission.builder()
                        .student(student)
                        .task(task)
                        .build());

        if (submission.getStudentRepoPath() != null) {
            fileService.deleteByFullRepoPath(submission.getStudentRepoPath());
        }

        String fileName = FileHelper.createSubmissionFileName(username, id);
        String path = fileService.uploadFile(Constants.Buckets.SUBMISSIONS, 
            fileName, 
            file.getInputStream(), file.getSize(), file.getContentType());

        submission.setStudentRepoPath(path);
        submission.setSubmittedAt(LocalDateTime.now());

        return ResponseEntity.ok(submissionRepository.save(submission));
    }

    @PostMapping(Constants.Routes.STUDENT_CHECK)
    @Operation(summary = "Check own solution")
    public ResponseEntity<GitComparisonResultDto> checkSolution(
            @PathVariable Long id,
            @RequestParam(defaultValue = "MERGED_GRAPH") ComparisonService.ReportType reportType,
            @RequestParam(defaultValue = "BRANCH") ComparisonService.ComparisonMethod method) throws IOException {
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("User {} initiated solution check for task {}: reportType={}, method={}", username, id, reportType, method);
        
        User student = userRepository.findByUsername(username).orElseThrow();
        Task task = taskRepository.findById(id).orElseThrow();

        StudentSubmission submission = submissionRepository.findByStudentAndTask(student, task)
                .orElseThrow(() -> new RuntimeException("No submission found"));

        GitComparisonResultDto result = comparisonService.compareRepositories(
                task.getReferenceRepoPath(), 
                submission.getStudentRepoPath(),
                reportType,
                method
        );
        return ResponseEntity.ok(result);
    }
}
