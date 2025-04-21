package com.main.Jora.unit;

import com.main.Jora.configs.CustomException;
import com.main.Jora.enums.Role;
import com.main.Jora.models.Project;
import com.main.Jora.models.Tag;
import com.main.Jora.models.User;
import com.main.Jora.models.UserProjectRole;
import com.main.Jora.repositories.ProjectRepository;
import com.main.Jora.repositories.TagRepository;
import com.main.Jora.repositories.UserProjectRoleReposirory;
import com.main.Jora.repositories.UserRepository;
import com.main.Jora.services.GroupService;
import com.main.Jora.services.ProjectService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GroupServiceTest {
    @Mock
    private UserProjectRoleReposirory userProjectRoleReposirory;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProjectService projectService;

    @InjectMocks
    private GroupService groupService;

    private User testUser;
    private Project testProject;
    private UserProjectRole testUserProjectRole;
    private Tag testTag;


    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");

        testProject = new Project();
        testProject.setId(1L);
        testProject.setHash("test_hash");

        testUserProjectRole = new UserProjectRole();
        testUserProjectRole.setUser(testUser);
        testUserProjectRole.setProject(testProject);

        testTag = new Tag();
        testTag.setId(1L);
        testTag.setName("testTag");
    }


    @Test
    void changeUserRole_PromoteParticipantToModerator_Successful() throws CustomException.UserNotFoundException {
        when(userProjectRoleReposirory.findRoleByUserAndProject(testUser.getId(), testProject.getId())).thenReturn(Role.ROLE_PARTICIPANT);
        when(userProjectRoleReposirory.getUserProjectRoleByUserAndProjectId(testUser, testProject.getId())).thenReturn(testUserProjectRole);

        User result = groupService.changeUserRole(testUser, testProject.getId(), "PROMOTE");

        assertEquals(testUser, result);
        assertEquals(Role.ROLE_MODERATOR, testUserProjectRole.getRole());
        verify(userProjectRoleReposirory, times(1)).save(testUserProjectRole);
    }

    @Test
    void changeUserRole_PromoteModeratorToLeader_Successful() throws CustomException.UserNotFoundException {
        testUserProjectRole.setRole(Role.ROLE_MODERATOR);
        when(userProjectRoleReposirory.findRoleByUserAndProject(testUser.getId(), testProject.getId())).thenReturn(Role.ROLE_MODERATOR);
        when(userProjectRoleReposirory.getUserProjectRoleByUserAndProjectId(testUser, testProject.getId())).thenReturn(testUserProjectRole);

        User result = groupService.changeUserRole(testUser, testProject.getId(), "PROMOTE");

        assertEquals(testUser, result);
        assertEquals(Role.ROLE_LEADER, testUserProjectRole.getRole());
        verify(userProjectRoleReposirory, times(1)).save(testUserProjectRole);
    }

    @Test
    void changeUserRole_DemoteLeaderToModerator_Successful() throws CustomException.UserNotFoundException {
        testUserProjectRole.setRole(Role.ROLE_LEADER);
        when(userProjectRoleReposirory.findRoleByUserAndProject(testUser.getId(), testProject.getId())).thenReturn(Role.ROLE_LEADER);
        when(userProjectRoleReposirory.existsMoreThanOneLeader(testProject.getId())).thenReturn(true);
        when(userProjectRoleReposirory.getUserProjectRoleByUserAndProjectId(testUser, testProject.getId())).thenReturn(testUserProjectRole);

        User result = groupService.changeUserRole(testUser, testProject.getId(), "DEMOTE");

        assertEquals(testUser, result);
        assertEquals(Role.ROLE_MODERATOR, testUserProjectRole.getRole());
        verify(userProjectRoleReposirory, times(1)).save(testUserProjectRole);
    }

    @Test
    void changeUserRole_DemoteLeaderToModerator_ThrowsExceptionWhenOnlyOneLeader() {
        testUserProjectRole.setRole(Role.ROLE_LEADER);
        when(userProjectRoleReposirory.findRoleByUserAndProject(testUser.getId(), testProject.getId())).thenReturn(Role.ROLE_LEADER);
        when(userProjectRoleReposirory.existsMoreThanOneLeader(testProject.getId())).thenReturn(false);

        assertThrows(CustomException.UserNotFoundException.class, () -> groupService.changeUserRole(testUser, testProject.getId(), "DEMOTE"));

        verify(userProjectRoleReposirory, never()).save(any(UserProjectRole.class));
    }


    @Test
    void changeUserRole_DemoteModeratorToParticipant_Successful() throws CustomException.UserNotFoundException {
        testUserProjectRole.setRole(Role.ROLE_MODERATOR);
        when(userProjectRoleReposirory.findRoleByUserAndProject(testUser.getId(), testProject.getId())).thenReturn(Role.ROLE_MODERATOR);
        when(userProjectRoleReposirory.getUserProjectRoleByUserAndProjectId(testUser, testProject.getId())).thenReturn(testUserProjectRole);

        User result = groupService.changeUserRole(testUser, testProject.getId(), "DEMOTE");

        assertEquals(testUser, result);
        assertEquals(Role.ROLE_PARTICIPANT, testUserProjectRole.getRole());
        verify(userProjectRoleReposirory, times(1)).save(testUserProjectRole);
    }

    @Test
    void changeUserRole_NoChange_SameRole() throws CustomException.UserNotFoundException {
        when(userProjectRoleReposirory.findRoleByUserAndProject(testUser.getId(), testProject.getId())).thenReturn(Role.ROLE_PARTICIPANT);

        User result = groupService.changeUserRole(testUser, testProject.getId(), "UNKNOWN_ACTION");

        assertEquals(testUser, result);
        verify(userProjectRoleReposirory, never()).save(any(UserProjectRole.class));
    }

    @Test
    void banUser_ToggleBanStatus_Successful() {
        testUserProjectRole.setBanned(false);
        when(userProjectRoleReposirory.getUserProjectRoleByUserAndProjectId(testUser, testProject.getId())).thenReturn(testUserProjectRole);
        when(userProjectRoleReposirory.save(testUserProjectRole)).thenReturn(testUserProjectRole);

        User result = groupService.banUser(testUser, testProject);

        assertEquals(testUser, result);
        Assertions.assertTrue(testUserProjectRole.isBanned());
        verify(userProjectRoleReposirory, times(1)).save(testUserProjectRole);
    }

    @Test
    void createTag_ValidInput_Successful() throws CustomException.LargeSizeException, CustomException.ObjectExistsException {
        String tagName = "validTag";
        when(projectRepository.findProjectByHash("test_hash")).thenReturn(testProject);
        when(tagRepository.findTagByTagNameAndProjectId(tagName, testProject.getId())).thenReturn(null);
        when(tagRepository.save(any(Tag.class))).thenAnswer(invocation -> {
            Tag savedTag = invocation.getArgument(0);
            savedTag.setId(1L); // Simulate auto-generated ID
            return savedTag;
        });
        when(projectRepository.save(testProject)).thenReturn(testProject);

        groupService.createTag("test_hash", tagName);

        verify(tagRepository, times(1)).save(any(Tag.class));
        verify(projectRepository, times(1)).save(testProject);
        assertTrue(testProject.getTags().stream().anyMatch(tag -> tag.getName().equals(tagName)));
    }

    @Test
    void createTag_TagNameTooLong_ThrowsLargeSizeException() {
        String tagName = "thisTagIsWayTooLongAndExceedsTheMaximumAllowedLength";

        assertThrows(CustomException.LargeSizeException.class, () -> groupService.createTag("test_hash", tagName));

        verify(tagRepository, never()).save(any(Tag.class));
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void createTag_TagAlreadyExists_ThrowsObjectExistsException() {
        String tagName = "existingTag";
        when(projectRepository.findProjectByHash("test_hash")).thenReturn(testProject);
        when(tagRepository.findTagByTagNameAndProjectId(tagName, testProject.getId())).thenReturn(testTag);

        assertThrows(CustomException.ObjectExistsException.class, () -> groupService.createTag("test_hash", tagName));

        verify(tagRepository, never()).save(any(Tag.class));
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void setTagToUser_TagNotAssigned_TagIsAssigned() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);
        when(projectService.findIdByHash("test_hash")).thenReturn(testProject.getId());
        when(tagRepository.findTagByTagNameAndProjectId("testTag", testProject.getId())).thenReturn(testTag);
        testUser.setTags(new ArrayList<>());

        groupService.setTagToUser("test@example.com", "test_hash", "testTag");

        assertTrue(testUser.getTags().contains(testTag));
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void setTagToUser_TagIsAssigned_TagIsRemoved() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);
        when(projectService.findIdByHash("test_hash")).thenReturn(testProject.getId());
        when(tagRepository.findTagByTagNameAndProjectId("testTag", testProject.getId())).thenReturn(testTag);
        List<Tag> tags = new ArrayList<>();
        tags.add(testTag);
        testUser.setTags(tags);

        groupService.setTagToUser("test@example.com", "test_hash", "testTag");

        assertFalse(testUser.getTags().contains(testTag));
        verify(userRepository, times(1)).save(testUser);
    }
}