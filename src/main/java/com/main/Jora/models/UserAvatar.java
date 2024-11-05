package com.main.Jora.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class UserAvatar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Lob
    private byte[] bytes;
    private Long userId;
}
