package org.master.diploma.backend.service;

import lombok.RequiredArgsConstructor;
import org.master.diploma.backend.dto.student_info.StudentLabDto;
import org.master.diploma.backend.dto.student_info.StudentTaskDto;
import org.master.diploma.backend.dto.user.UserResponseDto;
import org.master.diploma.backend.entity.LaboratoryWork;
import org.master.diploma.backend.entity.StudentSubmission;
import org.master.diploma.backend.entity.Task;
import org.master.diploma.backend.entity.User;
import org.master.diploma.backend.repository.LaboratoryWorkRepository;
import org.master.diploma.backend.repository.StudentSubmissionRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LaboratoryWorkService {
    private final LaboratoryWorkRepository laboratoryWorkRepository;
    private final StudentSubmissionRepository studentSubmissionRepository;

    public List<StudentLabDto> getLabsForStudent(User student) {
        List<LaboratoryWork> labs = laboratoryWorkRepository.findAll();
        List<StudentSubmission> submissions = studentSubmissionRepository.findByStudent(student);

        // Map task ID to submission for easy lookup
        Map<Long, StudentSubmission> taskSubmissions = submissions.stream()
                .collect(Collectors.toMap(s -> s.getTask().getId(), s -> s));

        return labs.stream()
                .sorted(Comparator.comparing(LaboratoryWork::getNumber))
                .map(lab -> {
                    // Each student has at most one task assigned per lab
                    StudentTaskDto assignedTaskDto = lab.getTasks().stream()
                            .filter(task -> taskSubmissions.containsKey(task.getId()))
                            .map(task -> mapToTaskDto(task, taskSubmissions.get(task.getId())))
                            .findFirst()
                            .orElse(null);

                    return mapToDto(lab, student, assignedTaskDto);
                })
                .collect(Collectors.toList());
    }

    private StudentLabDto mapToDto(LaboratoryWork lab, User student, StudentTaskDto taskDto) {
        return StudentLabDto.builder()
                .id(lab.getId())
                .number(lab.getNumber())
                .topic(lab.getTopic())
                .description(lab.getDescription())
                .maxGrade(lab.getMaxGrade())
                .student(UserResponseDto.from(student))
                .task(taskDto)
                .build();
    }

    private StudentTaskDto mapToTaskDto(Task task, StudentSubmission submission) {
        return StudentTaskDto.builder()
                .id(task.getId())
                .number(task.getNumber())
                .description(task.getDescription())
                .grade(submission.getGrade())
                .feedback(submission.getFeedback())
                .submittedAt(submission.getSubmittedAt())
                .status(determineStatus(submission))
                .build();
    }

    private String determineStatus(StudentSubmission submission) {
        if (submission.getGrade() != null) {
            return "GRADED";
        }
        if (submission.getStudentRepoPath() != null && !submission.getStudentRepoPath().equals("NOT_SUBMITTED")) {
            return "SUBMITTED";
        }
        return "ASSIGNED";
    }
}
