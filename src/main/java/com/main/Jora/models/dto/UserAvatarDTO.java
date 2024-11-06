package com.main.Jora.models.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class UserAvatarDTO {
    //Эти данные могут изменяться
    private Long userId;
    private String username;
    private String avatarUrl;
    public UserAvatarDTO(Long userId, String username){
        this.userId = userId;
        this.username = username;
    }
}
