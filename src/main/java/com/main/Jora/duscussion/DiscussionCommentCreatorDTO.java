package com.main.Jora.duscussion;

import lombok.Data;

import java.util.List;

@Data
public class DiscussionCommentCreatorDTO {
    private String text;
    private String projectHash;
    private List<Long> attachmentIds;
}
