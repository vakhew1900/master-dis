package org.git_tutor.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.master.diploma.backend.config.Constants;
import org.master.diploma.backend.dto.TaskDto;
import org.master.diploma.backend.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(Constants.Routes.TASKS)
@RequiredArgsConstructor
@Tag(name = "Task Management", description = "Endpoints for all authorized users to view tasks")
public class TaskController {
    private final TaskService taskService;

    @GetMapping
    @Operation(summary = "Get all tasks for authorized users")
    public ResponseEntity<List<TaskDto>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID for authorized users")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable Long id) {
        TaskDto task = taskService.getTaskById(id);
        return task != null ? ResponseEntity.ok(task) : ResponseEntity.notFound().build();
    }
}
