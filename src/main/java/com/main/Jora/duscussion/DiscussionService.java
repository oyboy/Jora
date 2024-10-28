package com.main.Jora.duscussion;

import com.main.Jora.models.Project;
import com.main.Jora.models.User;
import com.main.Jora.repositories.ProjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class DiscussionService {
    @Autowired
    DiscussionRepository discussionRepository;
    @Autowired
    ProjectRepository projectRepository;
    public DiscussionComment saveDiscussionComment(DiscussionCommentCreatorDTO createCommentDTO, User user){
        Project project = projectRepository.findProjectByHash(createCommentDTO.getProjectHash());

        DiscussionComment comment = new DiscussionComment();
        comment.setAuthor(user);
        comment.setText(createCommentDTO.getText());
        comment.setProject(project);
        comment.setCreatedAt(LocalDateTime.now());

        log.info("Saving discussion comment: {}", comment);
        discussionRepository.save(comment);
        return comment;
    }
    public List<DiscussionComment> getComments(String project_hash){
        Project project = projectRepository.findProjectByHash(project_hash);
        return discussionRepository.getDiscussionCommentsByProjectId(project.getId());
    }
}
