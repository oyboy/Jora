package com.main.Jora.duscussion;

import com.main.Jora.models.User;
import com.main.Jora.repositories.ProjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
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
    private GridFsTemplate gridFsTemplate;
    @Autowired
    DiscussionRepository discussionRepository;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    FileAttachmentRepository fileAttachmentRepository;
    public DiscussionComment saveDiscussionComment(DiscussionCommentCreatorDTO createCommentDTO, User user){
        DiscussionComment comment = new DiscussionComment();
        comment.setAuthorId(user.getId());
        comment.setAuthorName(user.getUsername());
        comment.setText(createCommentDTO.getText());
        comment.setProjectHash(createCommentDTO.getProjectHash());
        comment.setCreatedAt(LocalDateTime.now());

        if (createCommentDTO.getAttachmentIds() != null) {
            List<FileAttachment> attachments = fileAttachmentRepository.findAllById(createCommentDTO.getAttachmentIds());
            for (FileAttachment attachment : attachments) {
                attachment.setDiscussionCommentId(comment.getId());
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
        return discussionRepository.getDiscussionCommentsByProjectHash(project_hash);
    }
    public FileAttachment findFileByFileId(String id){
        log.info("Поиск файла по id: {}", id);
        return fileAttachmentRepository.findById(id).orElse(null);
    }
}
