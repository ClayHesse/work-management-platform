package com.clay.wmp.user.controller;

import com.clay.wmp.user.dto.RegisterUserRequest;
import com.clay.wmp.user.dto.UpdateRoleDto;
import com.clay.wmp.user.dto.UserDto;
import com.clay.wmp.user.entity.User;
import com.clay.wmp.user.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Fetching all users");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        log.info("Fetching user by id: {}", id);
        return userService.getUserById(id);
    }

    @GetMapping("/username/{username}")
    public UserDto getUserByUsername(@PathVariable String username) {
        log.info("Fetching user by username: {}", username);
        return userService.getUserByUsername(username);
    }

    @GetMapping("/email/{email}")
    public UserDto getUserByEmail(@PathVariable String email) {
        log.info("Fetching user by email: {}", email);
        return userService.getUserByEmail(email);
    }

    @GetMapping("/role/{role}")
    public List<UserDto> getUserByRole(@PathVariable User.UserRole role) {
        log.info("Fetching users by role: {}", role);
        return userService.getUsersByRole(role);
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody RegisterUserRequest registerUserRequest) {
        log.info("Attempting to create user with username: {}", registerUserRequest.username());
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(registerUserRequest));
    }

    @PutMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id, @Valid @RequestBody UserDto userDto) {
        log.info("Attempting to update user with id: {}", id);
        return userService.updateUser(id, userDto);
    }

    @PatchMapping("/{id}/role")
    public UserDto updateRole(@PathVariable Long id, @Valid @RequestBody UpdateRoleDto updateRoleDto) {
        log.info("Attempting to update role for user with id: {}", id);
        return userService.updateRole(id, updateRoleDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Attempting to delete user by id: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }


}
