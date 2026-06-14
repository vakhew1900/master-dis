package org.git_tutor.server.service;

import lombok.RequiredArgsConstructor;
import org.git_tutor.server.dto.student_info.StudentLabDto;
import org.git_tutor.server.dto.student_info.StudentTaskDto;
import org.git_tutor.server.entity.LaboratoryWork;
import org.git_tutor.server.entity.StudentSubmission;
import org.git_tutor.server.entity.Task;
import org.git_tutor.server.entity.User;
import org.git_tutor.server.repository.LaboratoryWorkRepository;
import org.git_tutor.server.repository.StudentSubmissionRepository;
import org.springframework.stereotype.Service;

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

        return labs.stream().map(lab -> {
            List<StudentTaskDto> taskDtos = lab.getTasks().stream()
                    .filter(task -> taskSubmissions.containsKey(task.getId()))
                    .map(task -> {
                        StudentSubmission submission = taskSubmissions.get(task.getId());
                        return StudentTaskDto.builder()
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

            return StudentLabDto.builder()
                    .id(lab.getId())
                    .number(lab.getNumber())
                    .topic(lab.getTopic())
                    .description(lab.getDescription())
                    .maxGrade(lab.getMaxGrade())
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
