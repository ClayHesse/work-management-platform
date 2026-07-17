package com.clay.wmp.user.service;

import com.clay.wmp.project.service.ProjectService;
import com.clay.wmp.task.service.TaskService;
import com.clay.wmp.team.service.TeamService;
import com.clay.wmp.user.repository.UserRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ProjectService projectService;
    @Mock
    private TeamService teamService;
    @Mock
    private TaskService taskService;

    @InjectMocks
    private UserService userService;
}
