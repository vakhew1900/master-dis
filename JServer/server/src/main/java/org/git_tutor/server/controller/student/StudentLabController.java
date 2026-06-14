package org.git_tutor.server.controller.student;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.git_tutor.server.config.Constants;
import org.git_tutor.server.entity.User;
import org.git_tutor.server.repository.UserRepository;
import org.git_tutor.server.dto.student_info.StudentLabDto;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.git_tutor.server.service.LaboratoryWorkService;

import java.util.List;

@RestController
@RequestMapping(Constants.Routes.STUDENT)
@RequiredArgsConstructor
@Tag(name = "Student Lab Operations", description = "Endpoints for students to view laboratory works and assignments")
public class StudentLabController {
    private final LaboratoryWorkService laboratoryWorkService;
    private final UserRepository userRepository;

    @GetMapping(Constants.Routes.STUDENT_LABS)
    @Operation(summary = "Get all laboratory works with assigned tasks for current student")
    public List<StudentLabDto> getLabs() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User student = userRepository.findByUsername(username).orElseThrow();
        return laboratoryWorkService.getLabsForStudent(student);
    }
}
