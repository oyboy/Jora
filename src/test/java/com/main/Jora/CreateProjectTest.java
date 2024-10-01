package com.main.Jora;

import com.main.Jora.repositories.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
public class CreateProjectTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ProjectRepository projectRepository;
    @BeforeEach
    public void setup() {
        projectRepository.deleteAll();
    }
    private String generateText(int n){
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < n; i++) builder.append(i);
        return builder.toString();
    }
    @Test
    public void testTitleCannotBeEmpty() throws Exception{
        this.mockMvc.perform(post("/home/create")
                .param("title", "")
                .param("description", "Some description"))
                .andExpect(model().attributeHasFieldErrors("project", "title"))
                .andExpect(model().attributeExists("errors"))
                .andExpect(view().name("create-project"));
    }
    @Test
    public void testBothFieldsCannotBeTooLong() throws Exception{
        this.mockMvc.perform(post("/home/create")
                        .param("title", generateText(51))
                        .param("description", generateText(260)))
                .andExpect(model().attributeHasFieldErrors("project", "title"))
                .andExpect(model().attributeHasFieldErrors("project", "description"))
                .andExpect(model().attributeExists("errors"))
                .andExpect(view().name("create-project"));
    }
    @Test
    public void testSuccessfulCreation() throws Exception {
        mockMvc.perform(post("/home/create")
                        .param("title", "Usual title")
                        .param("description", "Some description"))
                .andExpect(redirectedUrl("/home"));

        assertEquals(1, projectRepository.count());
    }
}
