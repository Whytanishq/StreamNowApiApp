package com.streamnow.api.controller;

import com.streamnow.api.dto.UserDto;
import com.streamnow.api.entity.User;
import com.streamnow.api.exception.ResourceNotFoundException;
import com.streamnow.api.repository.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@Tag(name = "User Management")
@RequiredArgsConstructor
public class UserAdminController {
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<Page<UserDto>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(userRepository.findAll(pageable).map(UserDto::fromEntity));
    }

    @PatchMapping("/{userId}/role")
    public ResponseEntity<UserDto> updateUserRole(
            @PathVariable Long userId,
            @RequestParam User.Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setRole(newRole);
        return ResponseEntity.ok(UserDto.fromEntity(userRepository.save(user)));
    }
}