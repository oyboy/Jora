package com.main.Jora.duscussion;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FileAttachmentDTO {
    private String fileName;
    private String downloadUrl;
}
