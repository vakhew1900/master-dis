package org.git_tutor.server.service;

import lombok.RequiredArgsConstructor;
import org.git_tutor.server.dto.TaskDto;
import org.git_tutor.server.entity.Task;
import org.git_tutor.server.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    public List<TaskDto> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public TaskDto getTaskById(Long id) {
        return taskRepository.findById(id)
                .map(this::convertToDto)
                .orElse(null);
    }

    private TaskDto convertToDto(Task task) {
        return TaskDto.builder()
                .id(task.getId())
                .number(task.getNumber())
                .description(task.getDescription())
                .referenceRepoPath(task.getReferenceRepoPath())
                .build();
    }
}
