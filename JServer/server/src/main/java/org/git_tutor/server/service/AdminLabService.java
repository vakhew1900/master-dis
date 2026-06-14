package org.git_tutor.server.service;

import lombok.RequiredArgsConstructor;
import org.master.diploma.backend.dto.admin.AdminLabDto;
import org.master.diploma.backend.dto.admin.AdminSubmissionDto;
import org.master.diploma.backend.dto.admin.AdminTaskDto;
import org.master.diploma.backend.entity.LaboratoryWork;
import org.master.diploma.backend.entity.Task;
import org.git_tutor.server.repository.LaboratoryWorkRepository;
import org.git_tutor.server.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminLabService {
    private final LaboratoryWorkRepository laboratoryWorkRepository;
    private final TaskRepository taskRepository;

    public List<AdminLabDto> getAllLabs() {
        return laboratoryWorkRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public AdminLabDto getLabById(Long id) {
        return laboratoryWorkRepository.findById(id)
                .map(this::convertToDto)
                .orElse(null);
    }

    public List<AdminTaskDto> getTasksByLabId(Long id) {
        return laboratoryWorkRepository.findById(id)
                .map(lab -> lab.getTasks().stream()
                        .map(this::convertToTaskDto)
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }

    @Transactional
    public AdminLabDto createLab(LaboratoryWork lab) {
        return convertToDto(laboratoryWorkRepository.save(lab));
    }

    @Transactional
    public AdminLabDto updateLab(Long id, LaboratoryWork labDetails) {
        return laboratoryWorkRepository.findById(id)
                .map(lab -> {
                    lab.setNumber(labDetails.getNumber());
                    lab.setTopic(labDetails.getTopic());
                    lab.setDescription(labDetails.getDescription());
                    lab.setMaxGrade(labDetails.getMaxGrade());
                    return convertToDto(laboratoryWorkRepository.save(lab));
                })
                .orElse(null);
    }

    @Transactional
    public void deleteLab(Long id) {
        laboratoryWorkRepository.deleteById(id);
    }

    public AdminSubmissionDto convertToSubmissionDto(org.master.diploma.backend.entity.StudentSubmission submission) {
        return AdminSubmissionDto.builder()
                .id(submission.getId())
                .taskId(submission.getTask().getId())
                .taskDescription(submission.getTask().getDescription())
                .student(org.master.diploma.backend.dto.user.UserResponseDto.from(submission.getStudent()))
                .studentRepoPath(submission.getStudentRepoPath())
                .grade(submission.getGrade())
                .feedback(submission.getFeedback())
                .submittedAt(submission.getSubmittedAt())
                .build();
    }

    private AdminLabDto convertToDto(LaboratoryWork lab) {
        return AdminLabDto.builder()
                .id(lab.getId())
                .number(lab.getNumber())
                .topic(lab.getTopic())
                .description(lab.getDescription())
                .maxGrade(lab.getMaxGrade())
                .tasks(lab.getTasks() != null ? lab.getTasks().stream()
                        .map(this::convertToTaskDto)
                        .collect(Collectors.toList()) : List.of())
                .build();
    }

    private AdminTaskDto convertToTaskDto(Task task) {
        return AdminTaskDto.builder()
                .id(task.getId())
                .number(task.getNumber())
                .description(task.getDescription())
                .referenceRepoPath(task.getReferenceRepoPath())
                .build();
    }
}
