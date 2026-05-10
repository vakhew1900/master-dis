package org.master.diploma.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.master.diploma.backend.config.Constants;
import org.master.diploma.backend.entity.Task;
import org.master.diploma.backend.repository.LaboratoryWorkRepository;
import org.master.diploma.backend.repository.TaskRepository;
import org.master.diploma.backend.service.MinioService;
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
    private final MinioService minioService;

    @PostMapping
    @Operation(summary = "Create a new task with reference repository")
    public ResponseEntity<Task> createTask(
            @RequestParam Integer number,
            @RequestParam String description,
            @RequestParam Long labId,
            @RequestParam("file") MultipartFile file) throws IOException {
        
        String path = minioService.uploadFile("references", 
                "lab_" + labId + "_task_" + number + "_" + System.currentTimeMillis() + ".zip", 
                file.getInputStream(), file.getSize(), file.getContentType());

        Task task = Task.builder()
                .number(number)
                .description(description)
                .lab(labRepository.findById(labId).orElseThrow())
                .referenceRepoPath(path)
                .build();

        return ResponseEntity.ok(taskRepository.save(task));
    }

    @PutMapping("/{id}")
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
            // Delete old file
            minioService.deleteByFullRepoPath(task.getReferenceRepoPath());
            
            // Upload new file
            String path = minioService.uploadFile("references", 
                    "lab_" + task.getLab().getId() + "_task_" + task.getNumber() + "_" + System.currentTimeMillis() + ".zip", 
                    file.getInputStream(), file.getSize(), file.getContentType());
            task.setReferenceRepoPath(path);
        }

        return ResponseEntity.ok(taskRepository.save(task));
    }
}
