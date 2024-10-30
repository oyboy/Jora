package com.main.Jora.duscussion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.Jora.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

        String hash = discussionComment.getProject().getHash();
        return new DiscussionCommentDTO(
                discussionComment.getText(),
                hash,
                discussionComment.getAuthor().getUsername(),
                discussionComment.getAuthor().getId(),
                discussionComment.getCreatedAt(),
                Optional.ofNullable(discussionComment.getAttachments())
                        .orElse(Collections.emptyList()) // Если attachments null, используем пустой список
                        .stream()
                        .map(attachment -> attachment.convertToDto(hash))
                        .collect(Collectors.toList())
        );
    }
    @PostMapping("/upload-files")
    public ResponseEntity<List<Long>> uploadFiles(@RequestParam("files") List<MultipartFile> files) {
        List<Long> fileIds = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                FileAttachment fileAttachment = discussionService.saveFileAttachment(file);
                fileIds.add(fileAttachment.getId());
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        return ResponseEntity.ok(fileIds);
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
    @GetMapping("/download/{fileId}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable("fileId") Long fileId) {
        FileAttachment fileAttachment = discussionService.findFileByFileId(fileId);
        //ava.lang.IllegalArgumentException: The Unicode character [Г] at code point [1,043] cannot be encoded as it i
        String encodedFileName = URLEncoder.encode(fileAttachment.getFileName(), StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileAttachment.getBytes());
    }
}