package com.clay.wmp.user.service;

import com.clay.wmp.common.exception.DuplicateResourceException;
import com.clay.wmp.common.exception.ResourceInUseException;
import com.clay.wmp.common.exception.ResourceNotFoundException;
import com.clay.wmp.project.service.ProjectService;
import com.clay.wmp.task.service.TaskService;
import com.clay.wmp.team.service.TeamService;
import com.clay.wmp.user.dto.*;
import com.clay.wmp.user.entity.User;
import com.clay.wmp.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProjectService projectService;
    private final TaskService taskService;
    private final TeamService teamService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       ProjectService projectService, TaskService taskService, TeamService teamService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.projectService = projectService;
        this.taskService = taskService;
        this.teamService = teamService;
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream().map(UserDto::fromUser).toList();
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserDto::fromUser)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    public UserDto getUserByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username)
                .map(UserDto::fromUser)
                .orElseThrow(() ->  new ResourceNotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    public UserDto getUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .map(UserDto::fromUser)
                .orElseThrow(() ->  new ResourceNotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    public List<UserDto> getUsersByRole(User.UserRole role) {
        return userRepository.findByRole(role).stream().map(UserDto::fromUser).toList();
    }

    @Transactional(readOnly = true)
    public UserDto getCurrentUser() {
        Long id = 1L; // TODO get current user id from auth
        return userRepository.findById(id)
                .map(UserDto::fromUser)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    public User getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    public User getUserReferenceById(Long id) {
        return userRepository.getReferenceById(id);
    }

    @Transactional
    public UserDto createUser(RegisterUserRequest registerUserRequest) {

        if(userRepository.existsByUsernameIgnoreCase(registerUserRequest.username())) {
            throw new DuplicateResourceException("Username already exists");
        }

        if(userRepository.existsByEmailIgnoreCase(registerUserRequest.email())) {
            throw new DuplicateResourceException("Email already exists");
        }

        // TODO we will need to add security to the password rules later, as well as force change.
        var newUser = new User(
                registerUserRequest.username(),
                registerUserRequest.name(),
                registerUserRequest.email(),
                passwordEncoder.encode(registerUserRequest.password())
        );
        return UserDto.fromUser(userRepository.save(newUser));
    }

    @Transactional
    public UserDto updateUser(Long id, UpdateUserProfileRequest updateUserProfileRequest) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if(!user.getUsername().equalsIgnoreCase(updateUserProfileRequest.username()) &&
                userRepository.existsByUsernameIgnoreCase(updateUserProfileRequest.username())) {
            log.debug("Username already exists, throwing exception");
            throw new DuplicateResourceException("Username already exists");
        }

        if(!user.getEmail().equalsIgnoreCase(updateUserProfileRequest.email()) &&
                userRepository.existsByEmailIgnoreCase(updateUserProfileRequest.email())) {
            throw new DuplicateResourceException("Email already exists");
        }

        user.setUsername(updateUserProfileRequest.username());
        user.setName(updateUserProfileRequest.name());
        user.setEmail(updateUserProfileRequest.email());
        return UserDto.fromUser(userRepository.save(user));
    }

    @Transactional
    public UserDto updateRole(Long id, UpdateUserRoleRequest updateUserRoleRequest) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setRole(updateUserRoleRequest.role());
        return UserDto.fromUser(userRepository.save(user));
    }

    @Transactional
    public UserDto updatePassword(Long id, UpdateUserPasswordRequest updateUserPasswordRequest) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getPassword().equals(passwordEncoder.encode(updateUserPasswordRequest.currentPassword()))) {
            throw new BadCredentialsException("Incorrect credentials"); //might need a custom bad credentials exception
        }
        //New Password checks should be added before just allowing an update.
        //validatePassword(updateUserPasswordRequest.password());
        user.setPassword(passwordEncoder.encode(updateUserPasswordRequest.newPassword()));
        return UserDto.fromUser(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found");
        }
        // DB Constraints can handle this but going through services is future consideration for microservices
        if (projectService.userHasProjects(id) || teamService.isTeamMember(id) || taskService.hasTasks(id)) {
            throw new ResourceInUseException("User cannot be deleted due to associated records");
        }
        userRepository.deleteById(id);
    }
}
