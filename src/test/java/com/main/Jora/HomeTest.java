package com.main.Jora;

import com.main.Jora.configs.CustomException;
import com.main.Jora.controllers.HomeController;
import com.main.Jora.models.Project;
import com.main.Jora.models.User;
import com.main.Jora.repositories.ProjectRepository;
import com.main.Jora.repositories.UserRepository;
import com.main.Jora.services.ProjectService;
import com.main.Jora.services.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
public class HomeTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    ProjectService projectService;
    @MockBean
    private UserService userService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private ProjectRepository projectRepository;
    private Project project;
    private User user;
    @BeforeEach
    public void setup(){
       project = new Project();
        project.setId(1L);
        project.setTitle("Valid project");
        project.setDescription("Some description");
        project.setHash("hash123");

        when(projectRepository.findProjectByHash("hash123"))
                .thenReturn(project);
    }

    @Test
    @WithMockUser
    public void testStartPage() throws Exception{
        mockMvc.perform(get("/home"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("My Projects")))
                .andExpect(view().name("home"));
    }
    @Test
    @WithMockUser
    public void testNewProjectPage() throws Exception{
        mockMvc.perform(get("/home/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("create-project"));
    }
    @Test
    @WithMockUser
    public void testCreatingProject_WhenValidInput_thenRedirectToTome() throws Exception{
        doNothing().when(projectService).saveProject(any(Project.class), any(User.class));

        mockMvc.perform(post("/home/create")
                        .param("title", project.getTitle())
                        .param("description", project.getDescription())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        verify(projectService, times(1)).saveProject(any(Project.class), eq(user));
    }
    @Test
    @WithMockUser
    public void testCreatingProject_WhenErrors_thenShowsCreateProjectPage() throws Exception{
        mockMvc.perform(post("/home/create")
                        .param("title", "")
                        .param("desctiption", "")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("create-project"))
                .andExpect(model().attributeExists("errors"));
        verify(projectService, times(0)).saveProject(any(Project.class), eq(user));
    }
    @Test
    @WithMockUser
    public void tesJoinToProject_WhenUserAlreadyJoined() throws Exception{
        String projectHash = "hash123";
        doThrow(CustomException.UserAlreadyJoinedException.class)
                .when(projectService)
                .addUserToProject(projectHash, user);
        mockMvc.perform(post("/home/join")
                        .param("project_hash", projectHash)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("userError"));
    }
    @Test
    @WithMockUser
    public void testJoinToProject_WhenUserBanned() throws Exception {
        String projectHash = "hash123";
        doThrow(CustomException.UserBannedException.class)
                .when(projectService)
                .addUserToProject(projectHash, user);

        mockMvc.perform(post("/home/join")
                        .param("project_hash", projectHash)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("userError"));
    }
    @Test
    @WithMockUser
    public void testGetProjects() throws Exception {
        User testUser = new User();
        testUser.setId(1L);
        given(userService.getUserById(testUser.getId())).willReturn(testUser);

        List<Project> projects = Collections.singletonList(new Project());
        given(projectService.getProjectsForUser(testUser)).willReturn(projects);

        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("projects", hasSize(1)));
    }
}