package com.clay.wmp.project.service;

import com.clay.wmp.common.exception.ResourceNotFoundException;
import com.clay.wmp.project.dto.CreateProjectRequest;
import com.clay.wmp.project.dto.ProjectDto;
import com.clay.wmp.project.dto.UpdateProjectRequest;
import com.clay.wmp.project.entity.Project;
import com.clay.wmp.project.repository.ProjectRepository;
import com.clay.wmp.team.entity.Team;
import com.clay.wmp.team.service.TeamService;
import com.clay.wmp.user.entity.User;
import com.clay.wmp.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserService userService;
    @Mock
    private TeamService teamService;

    @InjectMocks
    private ProjectService projectService;

    private static final String TEST_PROJECT_TITLE = "Test Project Title";
    private static final String TEST_PROJECT_DESCRIPTION = "Test Project Description";

    private static final Long TEST_OWNER_ID = 1L;
    private static final Long TEST_TEAM_ID = 2L;

    private static final String TEST_TEAM_NAME = "TestTeamName";

    private static final String TEST_USERNAME = "TestUsername";
    private static final String TEST_USER_NAME = "TestUserName";
    private static final String TEST_PASSWORD = "TestPassword";
    private static final String TEST_EMAIL = "TestEmail";

    private static final Project.ProjectStatus TEST_PROJECT_STATUS = Project.ProjectStatus.COMPLETED;

    private User createMockOwner () {
        return new User(TEST_USERNAME, TEST_USER_NAME, TEST_EMAIL, TEST_PASSWORD);
    }

    private Team createMockTeam () {
        return new Team(TEST_TEAM_NAME);
    }

    private CreateProjectRequest createMockCreateProjectRequest () {
        return new CreateProjectRequest(
                TEST_PROJECT_TITLE, TEST_PROJECT_DESCRIPTION, TEST_OWNER_ID, TEST_TEAM_ID);
    }

    private UpdateProjectRequest createMockUpdateProjectRequest () {
        return new UpdateProjectRequest(
                TEST_PROJECT_TITLE, TEST_PROJECT_DESCRIPTION, TEST_OWNER_ID, TEST_TEAM_ID, TEST_PROJECT_STATUS);
    }

    @Test
    @DisplayName("Get Project with invalid id throws ResourceNotFoundException")
    void getProjectById_WithInvalidId_ThrowsResourceNotFoundException () {
        when(projectRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.getProjectById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // Happy Path
    @Test
    @DisplayName("Creates a project with a full valid request.")
    void createProject_WithValidRequest_ReturnsProjectDto() {
        CreateProjectRequest createProjectRequest = createMockCreateProjectRequest();
        User mockOwner = createMockOwner();
        Team mockTeam = createMockTeam();
        Project savedProject = new Project(
                createProjectRequest.title(), createProjectRequest.description(), mockOwner, mockTeam);

        when(userService.getUserEntityById(TEST_OWNER_ID)).thenReturn(mockOwner);
        when(teamService.getTeamEntityById(TEST_TEAM_ID)).thenReturn(mockTeam);
        when(projectRepository.save(any(Project.class))).thenReturn(savedProject);

        ProjectDto result = projectService.createProject(createProjectRequest);

        assertThat(result.title()).isEqualTo(TEST_PROJECT_TITLE);
        assertThat(result.description()).isEqualTo(TEST_PROJECT_DESCRIPTION);
        assertThat(result.ownerName()).isEqualTo(TEST_USER_NAME);
        assertThat(result.teamName()).isEqualTo(TEST_TEAM_NAME);
        assertThat(result.status()).isEqualTo(Project.ProjectStatus.ACTIVE);

        verify(projectRepository).save(any(Project.class));
        verify(userService).getUserEntityById(TEST_OWNER_ID);
        verify(teamService).getTeamEntityById(TEST_TEAM_ID);
    }

    // Ensures business rule that a project cannot be created without a valid owner.
    @Test
    @DisplayName("createProject throws ResourceNotFound with no save when passed an invalid owner.")
    void createProject_WithInvalidOwner_ThrowsResourceNotFoundException() {
        CreateProjectRequest createProjectRequest = createMockCreateProjectRequest();

        when(userService.getUserEntityById(TEST_OWNER_ID)).thenThrow(
                new ResourceNotFoundException("no User found"));

        assertThatThrownBy(() ->
                projectService.createProject(createProjectRequest))
                .isInstanceOf(ResourceNotFoundException.class).hasMessage("no User found");

        verify(userService).getUserEntityById(TEST_OWNER_ID);
        verify(projectRepository, never()).save(any(Project.class));
    }

    // Ensures business rule that project can be created without being assigned to a team.
    @Test
    @DisplayName("Create Project succeeds with null team but otherwise valid request.")
    void createProject_WithNullTeam_ReturnsProjectDto() {
        CreateProjectRequest createProjectRequest = new CreateProjectRequest(
                TEST_PROJECT_TITLE,TEST_PROJECT_DESCRIPTION,TEST_OWNER_ID, null);
        User mockOwner = createMockOwner();

        Project savedProject = new Project(
                createProjectRequest.title(), createProjectRequest.description(), mockOwner, null);

        when(userService.getUserEntityById(TEST_OWNER_ID)).thenReturn(mockOwner);
        when(projectRepository.save(any(Project.class))).thenReturn(savedProject);

        ProjectDto result =  projectService.createProject(createProjectRequest);

        assertThat(result.title()).isEqualTo(TEST_PROJECT_TITLE);
        assertThat(result.description()).isEqualTo(TEST_PROJECT_DESCRIPTION);
        assertThat(result.ownerName()).isEqualTo(TEST_USER_NAME);
        assertThat(result.teamName()).isNull();
        assertThat(result.status()).isEqualTo(Project.ProjectStatus.ACTIVE);

        verify(userService).getUserEntityById(TEST_OWNER_ID);
        verify(teamService, never()).getTeamEntityById(any());
        verify(projectRepository).save(any(Project.class));
    }

    // Ensures that a project cannot be created with an invalid team id (null is not invalid).
    @Test
    @DisplayName("Create Project throws ResourceNotFound with invalid team but otherwise valid request.")
    void createProject_WithInvalidTeamId_ThrowsResourceNotFoundException() {
        CreateProjectRequest createProjectRequest = createMockCreateProjectRequest();
        User mockOwner = createMockOwner();

        when(userService.getUserEntityById(TEST_OWNER_ID)).thenReturn(mockOwner);
        when(teamService.getTeamEntityById(TEST_TEAM_ID)).thenThrow(new ResourceNotFoundException("no Team found"));

        assertThatThrownBy(() ->
                projectService.createProject(createProjectRequest))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(userService).getUserEntityById(TEST_OWNER_ID);
        verify(teamService).getTeamEntityById(TEST_TEAM_ID);
        verify(projectRepository, never()).save(any(Project.class));
    }

    //Happy Path
    @Test
    void updateProject_WithValidRequest_ReturnsProjectDto() {
        Long testProjectId = 999L;
        UpdateProjectRequest updateProjectRequest = createMockUpdateProjectRequest();
        User mockOwner = createMockOwner();
        Team mockTeam = createMockTeam();

        //Random entities to show values have been updated.
        User randomOwner = new User("randomUsername","randomUserName","randomEmail","random");
        Team randomTeam = new Team("randomTeamName");
        Project retrievedProject = new Project("randomTitle", "randomDesc", randomOwner, randomTeam);

        when(projectRepository.findById(testProjectId)).thenReturn(Optional.of(retrievedProject));
        when(userService.getUserEntityById(TEST_OWNER_ID)).thenReturn(mockOwner);
        when(teamService.getTeamEntityById(TEST_TEAM_ID)).thenReturn(mockTeam);
        when(projectRepository.save(any(Project.class))).thenReturn(retrievedProject);

        ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);

        projectService.updateProject(testProjectId, updateProjectRequest);

        verify(userService).getUserEntityById(TEST_OWNER_ID);
        verify(teamService).getTeamEntityById(TEST_TEAM_ID);
        verify(projectRepository).save(captor.capture());

        Project updatedProject = captor.getValue();

        assertThat(updatedProject.getTitle()).isEqualTo(TEST_PROJECT_TITLE);
        assertThat(updatedProject.getDescription()).isEqualTo(TEST_PROJECT_DESCRIPTION);
        assertThat(updatedProject.getOwner()).isEqualTo(mockOwner);
        assertThat(updatedProject.getAssignedTeam()).isEqualTo(mockTeam);
        assertThat(updatedProject.getStatus()).isEqualTo(TEST_PROJECT_STATUS);
    }

    //Invalid ProjectId throws ResourceNotFound
    @Test
    @DisplayName("UpdateProject with invalid projectId throws ResourceNotFound")
    void updateProject_WithInvalidId_ThrowsResourceNotFoundException() {
        UpdateProjectRequest updateProjectRequest = createMockUpdateProjectRequest();

        when(projectRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                projectService.updateProject(999L, updateProjectRequest))
                .isInstanceOf(ResourceNotFoundException.class).hasMessage("No project with Id");

        verify(userService, never()).getUserEntityById(any());
        verify(teamService, never()).getTeamEntityById(any());
        verify(projectRepository, never()).save(any(Project.class));
    }

    //Invalid Owner Id throws ResourceNotFound
    @Test
    @DisplayName("UpdateProject with invalid ownerId throws ResourceNotFound")
    void updateProject_WithInvalidOwnerId_ThrowsResourceNotFoundException() {
        UpdateProjectRequest updateProjectRequest = createMockUpdateProjectRequest();

        // Minimal project only used to reach owner lookup
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(new Project()));
        when(userService.getUserEntityById(anyLong())).thenThrow(new ResourceNotFoundException("no User found"));

        assertThatThrownBy(() ->
                projectService.updateProject(999L, updateProjectRequest))
                .isInstanceOf(ResourceNotFoundException.class).hasMessage("no User found");

        verify(userService).getUserEntityById(any());
        verify(teamService, never()).getTeamEntityById(any());
        verify(projectRepository, never()).save(any(Project.class));
    }

    //Invalid Team Id throws ResourceNotFound
    @Test
    @DisplayName("UpdateProject with invalid teamId throws ResourceNotFound")
    void updateProject_WithInvalidTeamId_ThrowsResourceNotFoundException() {
        UpdateProjectRequest updateProjectRequest = createMockUpdateProjectRequest();

        // Minimal project only used to reach team lookup
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(new Project()));
        when(userService.getUserEntityById(anyLong())).thenReturn(createMockOwner());
        when(teamService.getTeamEntityById(anyLong())).thenThrow(new ResourceNotFoundException("no Team found"));

        assertThatThrownBy(() ->
                projectService.updateProject(999L, updateProjectRequest))
                .isInstanceOf(ResourceNotFoundException.class).hasMessage("no Team found");

        verify(userService).getUserEntityById(any());
        verify(teamService).getTeamEntityById(any());
        verify(projectRepository, never()).save(any(Project.class));
    }

    //Updating with null team sets assignedTeam to null
    @Test
    @DisplayName("Update Project succeeds and sets assignedTeam to null with a null TeamId but otherwise valid request")
    void updateProject_WithNullTeamId_SetsAssignedTeamNull_AndReturnsWithoutError() {
        Long testProjectId = 999L;
        UpdateProjectRequest updateProjectRequest = new UpdateProjectRequest(
                TEST_PROJECT_TITLE, TEST_PROJECT_DESCRIPTION, TEST_OWNER_ID, null, TEST_PROJECT_STATUS);
        User mockOwner = createMockOwner();
        Team mockTeam = createMockTeam();
        Project retrievedProject = new Project("randomTitle", "randomDesc", mockOwner, mockTeam);

        when(projectRepository.findById(testProjectId)).thenReturn(Optional.of(retrievedProject));
        when(userService.getUserEntityById(TEST_OWNER_ID)).thenReturn(mockOwner);
        when(projectRepository.save(any(Project.class))).thenReturn(retrievedProject);

        ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);

        projectService.updateProject(testProjectId, updateProjectRequest);

        verify(teamService, never()).getTeamEntityById(any());
        verify(projectRepository).save(captor.capture());

        assertThat(captor.getValue().getAssignedTeam()).isNull();
    }

    @Test
    @DisplayName("Delete Project with invalid projectId throws ResourceNotFoundException")
    void deleteProject_WithInvalidProjectId_ThrowsResourceNotFoundException() {
        when(projectRepository.existsById(anyLong())).thenReturn(false);

        assertThatThrownBy(() -> projectService.deleteProject(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
