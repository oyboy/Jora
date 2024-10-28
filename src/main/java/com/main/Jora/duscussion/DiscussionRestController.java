package com.main.Jora.duscussion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.Jora.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/projects/{project_hash}/api/discussion")
public class DiscussionRestController {
    @Autowired
    DiscussionService discussionService;

    @ModelAttribute(name = "projectHash")
    public String getHash(@PathVariable("project_hash") String project_hash){
        return project_hash;
    }
    @MessageMapping("/projects/{project_hash}/discussion")
    @SendTo("/topic/projects/{project_hash}/discussion")
    public DiscussionCommentDTO sendComment(@Payload String jsonPayload,
                                            Principal principal){
        User currentUser = (User) ((Authentication) principal).getPrincipal();
        ObjectMapper objectMapper = new ObjectMapper();
        DiscussionCommentCreatorDTO createCommentDTO = null;
        try {
            createCommentDTO = objectMapper.readValue(jsonPayload, DiscussionCommentCreatorDTO.class);
        } catch (JsonProcessingException e) {
            System.out.println("Problem with converting json: " + e.getMessage());
        }
        DiscussionComment discussionComment = discussionService.saveDiscussionComment(createCommentDTO, currentUser);
        return new DiscussionCommentDTO(
                discussionComment.getText(),
                discussionComment.getProject().getHash(),
                discussionComment.getAuthor().getUsername(),
                discussionComment.getAuthor().getId(),
                discussionComment.getCreatedAt()
        );
    }
    @GetMapping("/comments")
    public ResponseEntity<List<DiscussionCommentDTO>> getCommentsForDiscussion(@PathVariable("project_hash") String project_hash){
        List<DiscussionCommentDTO> commentDTOS = discussionService.getComments(project_hash)
                .stream()
                .map(DiscussionComment::convertToDto)
                .collect(Collectors.toList());
        System.out.println("Found " + commentDTOS.size() + " comments in project " + project_hash);
        return ResponseEntity.ok(commentDTOS);
    }
}