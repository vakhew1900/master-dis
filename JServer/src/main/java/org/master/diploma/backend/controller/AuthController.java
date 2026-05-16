package org.master.diploma.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.master.diploma.backend.config.Constants;
import org.master.diploma.backend.entity.User;
import org.master.diploma.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import org.master.diploma.backend.dto.UserDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping(Constants.Routes.AUTH)
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user login and information")
public class AuthController {
    private final UserRepository userRepository;

    @PostMapping("/login")
    @Operation(summary = "Login user (Basic Auth verification)")
    public ResponseEntity<UserDto> login() {
        // Spring Security handles the check. If we are here, auth is successful.
        return getCurrentUser();
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user info")
    public ResponseEntity<UserDto> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(401).build();
        }

        return userRepository.findByUsername(auth.getName())
                .map(user -> ResponseEntity.ok(UserDto.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .role(user.getRole().name())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .middleName(user.getMiddleName())
                        .build()))
                .orElse(ResponseEntity.status(401).build());
    }
}
