package org.master.diploma.backend.service;

import lombok.RequiredArgsConstructor;
import org.master.diploma.backend.dto.SubmissionDto;
import org.master.diploma.backend.dto.UserDto;
import org.master.diploma.backend.entity.LaboratoryWork;
import org.master.diploma.backend.entity.StudentSubmission;
import org.master.diploma.backend.entity.Task;
import org.master.diploma.backend.entity.User;
import org.master.diploma.backend.repository.LaboratoryWorkRepository;
import org.master.diploma.backend.repository.StudentSubmissionRepository;
import org.master.diploma.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final LaboratoryWorkRepository laboratoryWorkRepository;
    private final StudentSubmissionRepository submissionRepository;

    public List<UserDto> getAllStudentsWithSubmissions() {
        List<User> students = userRepository.findByRole(User.Role.STUDENT);
        List<LaboratoryWork> allLabs = laboratoryWorkRepository.findAll();
        List<StudentSubmission> allSubmissions = submissionRepository.findAll();

        // Map<StudentId, Map<LabId, Submission>>
        Map<Long, Map<Long, StudentSubmission>> studentSubmissionsMap = allSubmissions.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getStudent().getId(),
                        Collectors.toMap(s -> s.getTask().getLab().getId(), s -> s, (s1, s2) -> s1)
                ));

        return students.stream().map(student -> {
            Map<Long, StudentSubmission> submissionsForStudent = studentSubmissionsMap.getOrDefault(student.getId(), Map.of());
            List<SubmissionDto> submissionDtos = new ArrayList<>();

            for (LaboratoryWork lab : allLabs) {
                StudentSubmission sub = submissionsForStudent.get(lab.getId());

                if (sub != null) {
                    submissionDtos.add(SubmissionDto.builder()
                            .labId(lab.getId())
                            .labNumber(lab.getNumber())
                            .taskId(sub.getTask().getId())
                            .taskName(sub.getTask().getDescription())
                            .submissionId(sub.getId())
                            .grade(sub.getGrade() != null ? sub.getGrade() : 0.0)
                            .isExists(true)
                            .build());
                } else {
                    submissionDtos.add(SubmissionDto.builder()
                            .labId(lab.getId())
                            .labNumber(lab.getNumber())
                            .taskId(0L)
                            .taskName("Task not assigned")
                            .submissionId(0L)
                            .grade(0.0)
                            .isExists(false)
                            .build());
                }
            }

            return UserDto.builder()
                    .id(student.getId())
                    .username(student.getUsername())
                    .firstName(student.getFirstName())
                    .lastName(student.getLastName())
                    .middleName(student.getMiddleName())
                    .role(student.getRole().name())
                    .submissions(submissionDtos)
                    .build();
        }).collect(Collectors.toList());
    }
}
