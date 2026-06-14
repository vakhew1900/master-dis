package org.git_tutor.server.service;

import lombok.RequiredArgsConstructor;
import org.git_tutor.server.dto.admin.AdminTaskDto;
import org.git_tutor.server.entity.Task;
import org.git_tutor.server.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminTaskService {
    private final TaskRepository taskRepository;

    public List<AdminTaskDto> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public AdminTaskDto getTaskById(Long id) {
        return taskRepository.findById(id)
                .map(this::convertToDto)
                .orElse(null);
    }

    public AdminTaskDto convertToDto(Task task) {
        return AdminTaskDto.builder()
                .id(task.getId())
                .number(task.getNumber())
                .description(task.getDescription())
                .referenceRepoPath(task.getReferenceRepoPath())
                .build();
    }
}
