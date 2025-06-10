package com.streamnow.api.service;

import com.streamnow.api.dto.AuthResponse;
import com.streamnow.api.dto.LoginRequest;
import com.streamnow.api.dto.SignupRequest;
import com.streamnow.api.entity.User;
import com.streamnow.api.exception.AuthException;
import com.streamnow.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthResponse register(SignupRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AuthException("Email already in use");
        }

        String hashedPassword = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());

        User.Role assignedRole;
        try {
            assignedRole = (request.getRole() != null && !request.getRole().isBlank())
                    ? User.Role.valueOf(request.getRole())
                    : User.Role.ROLE_USER;
        } catch (IllegalArgumentException e) {
            throw new AuthException("Invalid role provided");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(hashedPassword)
                .role(assignedRole)
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .role(user.getRole().name())
                .build();
    }


    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthException("Invalid credentials"));

        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new AuthException("Invalid credentials");
        }

        String token = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .role(user.getRole().name())
                .build();
    }
}