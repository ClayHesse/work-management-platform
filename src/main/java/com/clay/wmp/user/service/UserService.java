package com.clay.wmp.user.service;

import com.clay.wmp.common.exception.DuplicateResourceException;
import com.clay.wmp.common.exception.ResourceNotFoundException;
import com.clay.wmp.user.dto.RegisterUserRequest;
import com.clay.wmp.user.dto.UpdateRoleDto;
import com.clay.wmp.user.dto.UserDto;
import com.clay.wmp.user.dto.UserMapper;
import com.clay.wmp.user.entity.User;
import com.clay.wmp.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream().map(UserMapper::mapToDto).toList();
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserMapper::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    public UserDto getUserByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username)
                .map(UserMapper::mapToDto)
                .orElseThrow(() ->  new ResourceNotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    public UserDto getUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .map(UserMapper::mapToDto)
                .orElseThrow(() ->  new ResourceNotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    public List<UserDto> getUsersByRole(User.UserRole role) {
        return userRepository.findByRole(role).stream().map(UserMapper::mapToDto).toList();
    }

    @Transactional(readOnly = true)
    public User getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional
    public UserDto createUser(RegisterUserRequest registerUserRequest) {

        if(userRepository.existsByUsername(registerUserRequest.username())) {
            throw new DuplicateResourceException("Username already exists");
        }

        if(userRepository.existsByEmail(registerUserRequest.email())) {
            throw new DuplicateResourceException("Email already exists");
        }

        var newUser = new User(
                registerUserRequest.username(),
                registerUserRequest.name(),
                registerUserRequest.email(),
                passwordEncoder.encode(registerUserRequest.password())
        );
        return UserMapper.mapToDto(userRepository.save(newUser));
    }

    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        var user = userRepository.findById(id)
                .orElseThrow(RuntimeException::new);

        if(!user.getUsername().equalsIgnoreCase(userDto.username()) &&
                userRepository.existsByUsername(userDto.username())) {
            log.debug("Username already exists, throwing exception");
            throw new DuplicateResourceException("Username already exists");
        }

        if(!user.getEmail().equalsIgnoreCase(userDto.email()) &&
                userRepository.existsByEmail(userDto.email())) {
            throw new DuplicateResourceException("Email already exists");
        }

        user.setUsername(userDto.username());
        user.setName(userDto.name());
        user.setEmail(userDto.email());
        return UserMapper.mapToDto(userRepository.save(user));
    }

    public UserDto updateRole(Long id, UpdateRoleDto updateRoleDto) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setRole(updateRoleDto.role());
        return UserMapper.mapToDto(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        if(!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found");
        }
        userRepository.deleteById(id);
    }
}
