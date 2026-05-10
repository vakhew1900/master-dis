package org.master.diploma.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.master.diploma.backend.config.Constants;
import org.master.diploma.backend.entity.Task;
import org.master.diploma.backend.repository.TaskRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Constants.Routes.ADMIN_TASKS)
@RequiredArgsConstructor
@Tag(name = "Admin Task Management", description = "Endpoints for teachers to manage tasks")
public class AdminTaskController {
    private final TaskRepository taskRepository;

    @PostMapping
    @Operation(summary = "Create a new task")
    public Task createTask(@RequestBody Task task) {
        return taskRepository.save(task);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a task")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task taskDetails) {
        return taskRepository.findById(id)
                .map(task -> {
                    task.setNumber(taskDetails.getNumber());
                    task.setDescription(taskDetails.getDescription());
                    task.setReferenceRepoPath(taskDetails.getReferenceRepoPath());
                    return ResponseEntity.ok(taskRepository.save(task));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
