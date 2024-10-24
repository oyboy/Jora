package com.main.Jora.comments;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.Jora.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class CommentController {
    @Autowired
    private CommentService commentService;
    @MessageMapping("/projects/{project_hash}/tasks/{task_id}/comment")
    @SendTo("/topic/projects/{project_hash}/tasks/{task_id}/comment")
    public CommentDTO sendCommentWebSocket(@Payload String jsonPayload,
                                     @PathVariable("project_hash") String project_hash, //Конфликт пафа
                                     Principal principal) { //AuthPrinc не работает в сокетах
        User currentUser = (User) ((Authentication) principal).getPrincipal();
        ObjectMapper objectMapper = new ObjectMapper();
        CreateCommentDTO createCommentDTO = null;
        try {
            createCommentDTO = objectMapper.readValue(jsonPayload, CreateCommentDTO.class);
        } catch (JsonProcessingException e) {
            System.out.println("Problem with converting json: " + e.getMessage());
        }

        Comment savedComment = commentService.saveComment(createCommentDTO, currentUser);

        return new CommentDTO(
                savedComment.getText(),
                savedComment.getUser().getUsername(),
                savedComment.getCreatedAt()
        );
    }
    @GetMapping("/projects/{project_hash}/tasks/{task_id}/api/comments")
    public ResponseEntity<List<CommentDTO>> getCommentsByTaskId(@PathVariable("task_id") Long task_id) {
        List<CommentDTO> comments = commentService.getCommentsByTaskId(task_id).stream()
                .map(Comment::convertToDTO).
                collect(Collectors.toList());
        return ResponseEntity.ok(comments);
    }
}

