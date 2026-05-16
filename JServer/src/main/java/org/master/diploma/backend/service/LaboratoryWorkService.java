package org.master.diploma.backend.service;

import lombok.RequiredArgsConstructor;
import org.master.diploma.backend.dto.LaboratoryWorkDto;
import org.master.diploma.backend.dto.TaskDto;
import org.master.diploma.backend.entity.LaboratoryWork;
import org.master.diploma.backend.entity.StudentSubmission;
import org.master.diploma.backend.entity.Task;
import org.master.diploma.backend.entity.User;
import org.master.diploma.backend.repository.LaboratoryWorkRepository;
import org.master.diploma.backend.repository.StudentSubmissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LaboratoryWorkService {
    private final LaboratoryWorkRepository laboratoryWorkRepository;
    private final StudentSubmissionRepository studentSubmissionRepository;

    public List<LaboratoryWorkDto> getLabsForStudent(User student) {
        List<LaboratoryWork> labs = laboratoryWorkRepository.findAll();
        List<StudentSubmission> submissions = studentSubmissionRepository.findByStudent(student);

        // Map task ID to submission for easy lookup
        Map<Long, StudentSubmission> taskSubmissions = submissions.stream()
                .collect(Collectors.toMap(s -> s.getTask().getId(), s -> s));

        return labs.stream().map(lab -> {
            List<TaskDto> taskDtos = lab.getTasks().stream()
                    .filter(task -> taskSubmissions.containsKey(task.getId()))
                    .map(task -> {
                        StudentSubmission submission = taskSubmissions.get(task.getId());
                        return TaskDto.builder()
                                .id(task.getId())
                                .number(task.getNumber())
                                .description(task.getDescription())
                                .grade(submission.getGrade())
                                .feedback(submission.getFeedback())
                                .submittedAt(submission.getSubmittedAt())
                                .status(determineStatus(submission))
                                .build();
                    })
                    .collect(Collectors.toList());

            return LaboratoryWorkDto.builder()
                    .id(lab.getId())
                    .number(lab.getNumber())
                    .topic(lab.getTopic())
                    .description(lab.getDescription())
                    .tasks(taskDtos)
                    .build();
        }).collect(Collectors.toList());
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
