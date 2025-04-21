package com.main.Jora.comments;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.Jora.models.User;
import jakarta.persistence.TransactionRequiredException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/projects/{project_hash}/tasks/{task_id}/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;
    @MessageMapping("/projects/{project_hash}/tasks/{task_id}/comment")
    @SendTo("/topic/projects/{project_hash}/tasks/{task_id}/comment")
    public CommentDTO sendCommentWebSocket(@Payload String jsonPayload,//Конфликт пафа
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
        commentService.saveUnreadComments(savedComment, currentUser);

        return new CommentDTO(
                savedComment.getText(),
                savedComment.getUser().getUsername(),
                savedComment.getUser().getId(),
                savedComment.getCreatedAt(),
                savedComment.getId()
        );
    }
    @GetMapping
    public ResponseEntity<List<CommentDTO>> getCommentsByTaskId(@PathVariable("task_id") Long task_id) {
        List<CommentDTO> comments = commentService.getCommentsByTaskId(task_id).stream()
                .map(Comment::convertToDTO).
                collect(Collectors.toList());
        return ResponseEntity.ok(comments);
    }
    @PostMapping("/read")
    public ResponseEntity<UserCommentDTO> markAsRead(@RequestBody UserCommentDTO userCommentDTO){
        try{
            commentService.markAsRead(userCommentDTO);
        } catch (TransactionRequiredException ex){} //Если записи нет, то ничего не делать
        return ResponseEntity.ok().body(userCommentDTO);
    }
    @GetMapping("/unreadCount")
    public Long getUncreadCount(@AuthenticationPrincipal User user,
                                @PathVariable("task_id") Long task_id){
        return commentService.getUnreadCommentsCount(user, task_id);
    }
    @GetMapping("{commentId}/readers")
    public List<CommentReader> getReadersForComment(@PathVariable("commentId") Long commentId){
        return commentService.getReadersForComment(commentId);
    }
}

