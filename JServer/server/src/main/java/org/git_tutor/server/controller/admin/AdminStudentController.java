package org.git_tutor.server.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.git_tutor.git_logic.graph.dto.GitComparisonResultDto;
import org.git_tutor.server.config.Constants;
import org.git_tutor.server.dto.admin.AdminSubmissionDto;
import org.git_tutor.server.dto.user.UserCreateDto;
import org.git_tutor.server.dto.user.UserResponseDto;
import org.git_tutor.server.entity.StudentSubmission;
import org.git_tutor.server.entity.Task;
import org.git_tutor.server.entity.User;
import org.git_tutor.server.repository.StudentSubmissionRepository;
import org.git_tutor.server.repository.TaskRepository;
import org.git_tutor.server.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(Constants.Routes.ADMIN + "/students")
@RequiredArgsConstructor
@Tag(name = "Admin Student Management", description = "Endpoints for teachers to manage students and their assignments")
public class AdminStudentController {
    private final UserRepository userRepository;
    private final StudentSubmissionRepository submissionRepository;
    private final TaskRepository taskRepository;
    private final org.git_tutor.server.service.FileService fileService;
    private final org.git_tutor.server.service.ComparisonService comparisonService;
    private final org.git_tutor.server.service.UserService userService;
    private final org.git_tutor.server.service.AdminLabService adminLabService;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @GetMapping
    @Operation(summary = "Get all students with their submissions")
    public List<UserResponseDto> getAllStudents() {
        return userService.getAllStudentsWithSubmissions();
    }

    @PostMapping("/submissions/{submissionId}/check")
    @Operation(summary = "Check submission solution (Admin)")
    public ResponseEntity<GitComparisonResultDto> checkSubmission(
            @PathVariable Long submissionId,
            @RequestParam(defaultValue = "MERGED_GRAPH") org.git_tutor.server.service.ComparisonService.ReportType reportType,
            @RequestParam(defaultValue = "BRANCH") org.git_tutor.server.service.ComparisonService.ComparisonMethod method) throws java.io.IOException {
        
        StudentSubmission submission = submissionRepository.findById(submissionId).orElseThrow();
        
        GitComparisonResultDto result = comparisonService.compareRepositories(
                submission.getTask().getReferenceRepoPath(), 
                submission.getStudentRepoPath(),
                reportType,
                method
        );
        return ResponseEntity.ok(result);
    }

    @PostMapping
    @Operation(summary = "Create a new student")
    public ResponseEntity<UserResponseDto> createStudent(@RequestBody UserCreateDto userDto) {
        User student = User.builder()
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .middleName(userDto.getMiddleName())
                .role(User.Role.STUDENT)
                .build();
        return ResponseEntity.ok(UserResponseDto.from(userRepository.save(student)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{studentId}/submissions")
    @Operation(summary = "Get all submissions for a specific student")
    public ResponseEntity<List<AdminSubmissionDto>> getStudentSubmissions(@PathVariable Long studentId) {
        return userRepository.findById(studentId)
                .map(student -> ResponseEntity.ok(
                        submissionRepository.findByStudent(student).stream()
                                .map(adminLabService::convertToSubmissionDto)
                                .collect(Collectors.toList())
                ))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/submissions")
    @Operation(summary = "Get all student submissions")
    public List<AdminSubmissionDto> getAllSubmissions() {
        return submissionRepository.findAll().stream()
                .map(adminLabService::convertToSubmissionDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/{studentId}/assign/{taskId}")
    @Operation(summary = "Assign task to student")
    public ResponseEntity<AdminSubmissionDto> assignTask(@PathVariable Long studentId, @PathVariable Long taskId) {
        User student = userRepository.findById(studentId).orElseThrow();
        Task task = taskRepository.findById(taskId).orElseThrow();

        StudentSubmission submission = submissionRepository.findByStudentAndTask(student, task)
                .orElse(StudentSubmission.builder()
                        .student(student)
                        .task(task)
                        .submittedAt(LocalDateTime.now()) // Placeholder until actual submission
                        .studentRepoPath("NOT_SUBMITTED")
                        .build());
        
        return ResponseEntity.ok(adminLabService.convertToSubmissionDto(submissionRepository.save(submission)));
    }

    @DeleteMapping("/submissions/{submissionId}")
    @Operation(summary = "Remove task assignment/submission and its file")
    public ResponseEntity<Void> removeAssignment(@PathVariable Long submissionId) throws java.io.IOException {
        StudentSubmission submission = submissionRepository.findById(submissionId).orElseThrow();
        if (submission.getStudentRepoPath() != null && !submission.getStudentRepoPath().equals("NOT_SUBMITTED")) {
            fileService.deleteByFullRepoPath(submission.getStudentRepoPath());
        }
        submissionRepository.delete(submission);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/submissions/{submissionId}/grade")
    @Operation(summary = "Grade student submission")
    public ResponseEntity<AdminSubmissionDto> gradeSubmission(
            @PathVariable Long submissionId, 
            @RequestParam Double grade,
            @RequestParam(required = false) String feedback) {
        
        StudentSubmission submission = submissionRepository.findById(submissionId).orElseThrow();
        
        Double maxGrade = submission.getTask().getLab().getMaxGrade();
        if (grade > maxGrade) {
            throw new IllegalArgumentException("Grade cannot exceed max grade: " + maxGrade);
        }

        submission.setGrade(grade);
        submission.setFeedback(feedback);
        return ResponseEntity.ok(adminLabService.convertToSubmissionDto(submissionRepository.save(submission)));
    }
}
