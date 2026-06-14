package org.git_tutor.server.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.git_tutor.server.config.Constants;
import org.git_tutor.server.entity.Task;
import org.git_tutor.server.repository.LaboratoryWorkRepository;
import org.git_tutor.server.repository.TaskRepository;
import org.git_tutor.server.service.FileService;
import org.git_tutor.server.support.FileHelper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping(Constants.Routes.ADMIN_TASKS)
@RequiredArgsConstructor
@Tag(name = "Admin Task Management", description = "Endpoints for teachers to manage tasks")
public class AdminTaskController {
    private final TaskRepository taskRepository;
    private final LaboratoryWorkRepository labRepository;
    private final FileService fileService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create a new task with reference repository")
    public ResponseEntity<Task> createTask(
            @RequestParam Integer number,
            @RequestParam String description,
            @RequestParam Long labId,
            @RequestParam("file") MultipartFile file) throws IOException {
        
        String fileName = FileHelper.createLabFileName(labId, number);
        String path = fileService.uploadFile(Constants.Buckets.REFERENCES, 
                fileName, 
                file.getInputStream(), file.getSize(), file.getContentType());

        Task task = Task.builder()
                .number(number)
                .description(description)
                .lab(labRepository.findById(labId).orElseThrow())
                .referenceRepoPath(path)
                .build();

        return ResponseEntity.ok(taskRepository.save(task));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update a task and optionally its reference repository")
    public ResponseEntity<Task> updateTask(
            @PathVariable Long id,
            @RequestParam(required = false) Integer number,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) MultipartFile file) throws IOException {
        
        Task task = taskRepository.findById(id).orElseThrow();
        
        if (number != null) task.setNumber(number);
        if (description != null) task.setDescription(description);
        
        if (file != null) {
            fileService.deleteByFullRepoPath(task.getReferenceRepoPath());
            
            String fileName = FileHelper.createLabFileName(task.getLab().getId(), task.getNumber());
            String path = fileService.uploadFile(Constants.Buckets.REFERENCES, 
                    fileName, 
                    file.getInputStream(), file.getSize(), file.getContentType());
            task.setReferenceRepoPath(path);
        }

        return ResponseEntity.ok(taskRepository.save(task));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task and its reference repository")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) throws IOException {
        Task task = taskRepository.findById(id).orElseThrow();
        fileService.deleteByFullRepoPath(task.getReferenceRepoPath());
        taskRepository.delete(task);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(summary = "Get all tasks")
    public ResponseEntity<Iterable<Task>> getAllTasks() {
        return ResponseEntity.ok(taskRepository.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return taskRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
