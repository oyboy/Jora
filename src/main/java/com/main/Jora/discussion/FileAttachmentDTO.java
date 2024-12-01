package com.main.Jora.discussion;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileAttachmentDTO {
    private String fileName;
    private String downloadUrl;
}
