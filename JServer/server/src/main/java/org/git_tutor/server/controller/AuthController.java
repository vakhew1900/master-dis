package org.git_tutor.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.master.diploma.backend.config.Constants;
import org.master.diploma.backend.dto.user.LoginRequestDto;
import org.master.diploma.backend.dto.user.UserResponseDto;
import org.git_tutor.server.repository.UserRepository;
import org.master.diploma.backend.security.JwtUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(Constants.Routes.AUTH)
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user login and information")
public class AuthController {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    @Operation(summary = "Login user (JSON credentials)")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = jwtUtils.generateToken(userDetails);

            return userRepository.findByUsername(userDetails.getUsername())
                    .map(user -> ResponseEntity.ok(Map.of(
                            "token", jwt,
                            "user", UserResponseDto.from(user)
                    )))
                    .orElse(ResponseEntity.status(401).build());
        } catch (AuthenticationException e) {
            throw e; // Rethrow to let GlobalExceptionHandler handle it with 401
        }
    }
    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user info")
    public ResponseEntity<UserResponseDto> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(401).build();
        }

        return userRepository.findByUsername(auth.getName())
                .map(user -> ResponseEntity.ok(UserResponseDto.builder()
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
