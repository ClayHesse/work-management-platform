package com.clay.wmp.user.controller;

import com.clay.wmp.user.dto.UserResponse;
import com.clay.wmp.user.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public List<UserResponse> getAllUsers() {
        return userService.findAllUsers();
    }
}
