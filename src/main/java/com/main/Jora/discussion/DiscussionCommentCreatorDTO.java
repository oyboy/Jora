package com.main.Jora.discussion;

import lombok.Data;

import java.util.List;

@Data
public class DiscussionCommentCreatorDTO {
    private String text;
    private String projectHash;
    private List<String> attachmentIds;
}