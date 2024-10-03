package com.main.Jora.security;

import com.main.Jora.repositories.ProjectRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PathTraverseDef {
    @Autowired
    private final HttpSession httpSession;
    @Autowired
    private final ProjectRepository projectRepository;
    public PathTraverseDef(HttpSession httpSession, ProjectRepository projectRepository){
        this.httpSession = httpSession;
        this.projectRepository = projectRepository;
    }
    public String preventingEntryIntoProject(Long project_id){
        if (!projectRepository.existsById(project_id)) { //Проверка на наличие несуществующего проекта
            String prevPage = (String) httpSession.getAttribute("previousPage");
            if (prevPage != null) {
                return "redirect:" + prevPage;
            }
            return "redirect:/home";
        }
        return null;
    }
}
