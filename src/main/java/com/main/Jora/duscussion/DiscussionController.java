package com.main.Jora.duscussion;

import com.main.Jora.models.User;
import com.main.Jora.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("/projects/{project_hash}/discussion")
public class DiscussionController {
    @Autowired
    UserService userService;
    @ModelAttribute(name = "projectHash")
    public String getHash(@PathVariable("project_hash") String project_hash){
        return project_hash;
    }
    @ModelAttribute(name = "user")
    public User getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            //Теперь создаётся запрос к базе для получения актуальных даннных
            return userService.getUserById(user.getId());
        }
        return new User();
    }
    @GetMapping
    public String openDiscussion(Model model,
                                 @AuthenticationPrincipal User currentUser){
        if (currentUser == null || currentUser.getId() == null) model.addAttribute("errorUserId", "User not found");
        model.addAttribute("currentUser", currentUser);
        return "discussion";
    }
}
