package com.main.Jora.duscussion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.Jora.models.User;
import com.main.Jora.models.dto.UserAvatarDTO;
import com.main.Jora.services.UserService;
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
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/projects/{project_hash}/api/discussion")
public class DiscussionRestController {
    @Autowired
    DiscussionService discussionService;
    @Autowired
    private UserService userService;

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

        String hash = createCommentDTO.getProjectHash();
        return discussionComment.convertToDto(hash,
                new UserAvatarDTO(currentUser.getId(), currentUser.getUsername()));
    }

    @PostMapping("/upload-files")
    public ResponseEntity<List<String>> uploadFiles(@RequestParam("files") List<MultipartFile> files) {
        List<String> fileIds = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                fileIds.add(discussionService.saveFileAttachment(file));
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
                //Oh, jesus
                .map(attachment -> attachment.convertToDto(project_hash,
                        new UserAvatarDTO(attachment.getAuthorId(),
                                userService.getUserById(attachment.getAuthorId()).getUsername())))
                .collect(Collectors.toList());
        System.out.println("Found " + commentDTOS.size() + " comments in project " + project_hash);
        return ResponseEntity.ok(commentDTOS);
    }
    @GetMapping("/download/{fileId}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable("fileId") String fileId) {
        FileAttachment fileAttachment = discussionService.findFileByFileId(fileId);
        //ava.lang.IllegalArgumentException: The Unicode character [Г] at code point [1,043] cannot be encoded as it i
        if (fileAttachment == null) return ResponseEntity.badRequest().body("File deleted".getBytes());
        String encodedFileName = URLEncoder.encode(fileAttachment.getFileName(), StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileAttachment.getBytes());
    }
}