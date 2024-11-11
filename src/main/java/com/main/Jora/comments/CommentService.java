package com.main.Jora.comments;

import com.main.Jora.models.Task;
import com.main.Jora.models.User;
import com.main.Jora.repositories.TaskRepository;
import com.main.Jora.repositories.UserTaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    UserCommentRepository userCommentRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserTaskRepository userTaskRepository;
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
    @Transactional
    public void markAsRead(UserCommentDTO userCommentDTO){
        log.info("Marking comment ID {} as read.", userCommentDTO.getCommentId());
        userCommentRepository.updateCommentAsRead(
                userCommentDTO.getUserId(),
                userCommentDTO.getTaskId(),
                userCommentDTO.getCommentId()
        );
    }
    //Отображаются только те пользователи, которые привязаны к задаче
    public void saveUnreadComments(Comment savedComment, User currentUser){
        log.info("Saving unread comments");
        List<User> usersForTask = userTaskRepository.getUsersByTaskId(savedComment.getTask().getId());
        for (User u : usersForTask){
            if (u.equals(currentUser)) continue; //Для отправителя не нужно сохранять непрочитанное сообщение
            UserCommentDTO userCommentDTO = new UserCommentDTO(u.getId(), savedComment.getId(), savedComment.getTask().getId());
            log.info("Saving user-comment dto: {}", userCommentDTO);
            userCommentRepository.save(userCommentDTO);
        }
    }
    public Long getUnreadCommentsCount(User user, Long taskId){
        return userCommentRepository.getUnreadCommentsCount(user.getId(), taskId);
    }
    public List<CommentReader> getReadersForComment(Long commentId){
        log.info("Finding readers in {} comment", commentId);
        List<CommentReader> foundUsers = userCommentRepository.getReadersForComment(commentId);
        log.info("Found: {}", foundUsers);
        return foundUsers;
    }
    //Автоудаление прочитанных комментариев
    @Transactional
    @Scheduled(fixedRate = 172_800_000) //1 секунда = 1000 / 172_800_800 = 48 часов
    public void cleanReadComments() {
        log.info("removing read comments from db");
        userCommentRepository.deleteReadComments();
    }
}
