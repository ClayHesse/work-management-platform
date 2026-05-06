package com.clay.wmp.user.service;

import com.clay.wmp.user.dto.UserMapper;
import com.clay.wmp.user.dto.UserResponse;
import com.clay.wmp.user.entity.User;
import com.clay.wmp.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponse> findAllUsers() {
        return userRepository.findAll()
                .stream().map(UserMapper::mapToResponse).toList();
    }
}
