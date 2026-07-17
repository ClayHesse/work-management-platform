package com.clay.wmp.user.controller;

import com.clay.wmp.user.dto.*;
import com.clay.wmp.user.entity.User;
import com.clay.wmp.user.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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

    @GetMapping("/me")
    public UserDto getCurrentUser() {
        log.info("Fetching current user information");
        return userService.getCurrentUser();
    }

    @GetMapping("/role/{role}")
    public List<UserDto> getUserByRole(@PathVariable User.UserRole role) {
        log.info("Fetching users by role: {}", role);
        return userService.getUsersByRole(role);
    }

    // This will become admin create user later once auth register and login is added.
    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody RegisterUserRequest registerUserRequest) {
        log.info("Attempting to create user with username: {}", registerUserRequest.username());
        var user = userService.createUser(registerUserRequest);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}").buildAndExpand(user.id()).toUri();
        return ResponseEntity.created(location).body(user);
    }

    @PutMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id,
                              @Valid @RequestBody UpdateUserProfileRequest updateUserProfileRequest) {
        log.info("Attempting to update user with id: {}", id);
        return userService.updateUser(id, updateUserProfileRequest);
    }

    @PutMapping("/me")
    public UserDto updateCurrentUser(/*@AuthenticationPrincipal jwt,*/ @Valid @RequestBody UpdateUserProfileRequest updateUserProfileRequest) {
        log.info("Attempting to update current user with username: {}", updateUserProfileRequest.username());
        Long id = 1L; // TODO Pulled from auth object
        return userService.updateUser(id, updateUserProfileRequest);
    }

    @PatchMapping("/{id}/role")
    public UserDto updateRole(@PathVariable Long id, @Valid @RequestBody UpdateUserRoleRequest updateUserRoleRequest) {
        log.info("Attempting to update role for user with id: {}", id);
        return userService.updateRole(id, updateUserRoleRequest);
    }

    @PatchMapping("/me/password")
    public UserDto updateUserPassword(/*@AuthenticationPrincipal jwt,*/ @Valid @RequestBody UpdateUserPasswordRequest updateUserPasswordRequest) {
        Long id = 1L; //TODO id from auth object
        log.info("Attempting to update password for user with id: {}", id);
        return userService.updatePassword(id, updateUserPasswordRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Attempting to delete user by id: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }


}
