package com.main.Jora.discussion;

import com.main.Jora.models.User;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Service
@Slf4j
@Transactional
public class DiscussionService {
    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    DiscussionRepository discussionRepository;
    public DiscussionComment saveDiscussionComment(DiscussionCommentCreatorDTO createCommentDTO, User user){
        DiscussionComment comment = new DiscussionComment();
        comment.setAuthorId(user.getId());
        comment.setText(createCommentDTO.getText());
        comment.setProjectHash(createCommentDTO.getProjectHash());
        comment.setCreatedAt(LocalDateTime.now());

        List<FileAttachment> attachments = new ArrayList<>();
        if (createCommentDTO.getAttachmentIds() != null) {
            for (String attachmentId : createCommentDTO.getAttachmentIds()) {
                GridFSFile gridFsFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(attachmentId)));
                FileAttachment fileAttachment = new FileAttachment();
                fileAttachment.setId(attachmentId);
                fileAttachment.setFileName(gridFsFile.getFilename());
                fileAttachment.setUploadedAt(LocalDateTime.now());

                attachments.add(fileAttachment);
            }
        }
        comment.setAttachments(attachments);

        log.info("Saving discussion comment: {}", comment.getText());
        discussionRepository.save(comment);
        return comment;
    }
    public String saveFileAttachment(MultipartFile file) throws IOException {
        log.info("Saving attachment: {}", file.getOriginalFilename());
        InputStream inputStream = file.getInputStream();
        ObjectId fileId = gridFsTemplate.store(inputStream, file.getOriginalFilename());
        return fileId.toHexString();
    }
    public List<DiscussionComment> getComments(String project_hash){
        return discussionRepository.getDiscussionCommentsByProjectHash(project_hash);
    }
    public FileAttachment findFileByFileId(String id) {
        log.info("Поиск файла по id: {}", id);
        try {
            ObjectId objectId = new ObjectId(id);
            GridFSFile gridFsFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(objectId)));
            if (gridFsFile != null) {
                GridFsResource resource = gridFsTemplate.getResource(gridFsFile);
                FileAttachment fileAttachment = new FileAttachment();
                fileAttachment.setId(id);
                fileAttachment.setFileName(resource.getFilename());
                fileAttachment.setBytes(resource.getInputStream().readAllBytes());
                return fileAttachment;
            } else {
                log.error("Файл с ID {} не найден", id);
            }
        } catch (IOException io) {
            log.error(io.getMessage());
        }
        return null;
    }
    public void deleteAttachmentById(String id){
        gridFsTemplate.delete(new Query(Criteria.where("_id").is(id)));
    }
}