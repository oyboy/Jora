package com.main.Jora.duscussion;

import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class DiscussionUserDTO {
    //Эти данные могут изменяться
    private String username;
    private String avatarUrl;
    public DiscussionUserDTO(String username){
        this.username = username;
    }
}
