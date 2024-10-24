package com.main.Jora.comments;

import com.main.Jora.models.Task;
import com.main.Jora.models.User;
import com.main.Jora.repositories.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private TaskRepository taskRepository;
    public List<Comment> getCommentsByTaskId(Long taskId) {
        return commentRepository.findAllByTaskId(taskId);
    }

    public Comment saveComment(CreateCommentDTO createCommentDTO, User user) {
        log.info("Received commentData: {}", createCommentDTO);
        Task task = taskRepository.findById(createCommentDTO.getTaskId()).orElse(null);

        Comment comment = new Comment();
        comment.setText(createCommentDTO.getText());
        comment.setTask(task);
        comment.setUser(user);
        comment.setCreatedAt(LocalDateTime.now());

        log.info("Saving new comment: {}", comment);
        commentRepository.save(comment);
        return comment;
    }
}
