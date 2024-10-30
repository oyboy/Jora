package com.main.Jora.duscussion;

import com.main.Jora.models.Project;
import com.main.Jora.models.User;
import com.main.Jora.repositories.ProjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@Transactional
public class DiscussionService {
    @Autowired
    DiscussionRepository discussionRepository;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    FileAttachmentRepository fileAttachmentRepository;
    public DiscussionComment saveDiscussionComment(DiscussionCommentCreatorDTO createCommentDTO, User user){
        Project project = projectRepository.findProjectByHash(createCommentDTO.getProjectHash());

        DiscussionComment comment = new DiscussionComment();
        comment.setAuthor(user);
        comment.setText(createCommentDTO.getText());
        comment.setProject(project);
        comment.setCreatedAt(LocalDateTime.now());

        if (createCommentDTO.getAttachmentIds() != null) {
            List<FileAttachment> attachments = (List<FileAttachment>) fileAttachmentRepository.findAllById(createCommentDTO.getAttachmentIds());
            for (FileAttachment attachment : attachments) {
                attachment.setDiscussionComment(comment);
            }
            comment.setAttachments(attachments);
        }

        log.info("Saving discussion comment: {}", comment.getText());
        discussionRepository.save(comment);
        return comment;
    }
    public FileAttachment saveFileAttachment(MultipartFile file) throws IOException {
        FileAttachment fileAttachment = new FileAttachment();
        fileAttachment.setFileName(file.getOriginalFilename());
        fileAttachment.setBytes(file.getBytes());
        fileAttachment.setUploadedAt(LocalDateTime.now());

        fileAttachmentRepository.save(fileAttachment);
        return fileAttachment;
    }
    public List<DiscussionComment> getComments(String project_hash){
        Project project = projectRepository.findProjectByHash(project_hash);
        return discussionRepository.getDiscussionCommentsByProjectId(project.getId());
    }
    public FileAttachment findFileByFileId(Long id){
        log.info("Поиск файла по id: {}", id);
        return fileAttachmentRepository.findById(id).orElse(null);
    }
}
