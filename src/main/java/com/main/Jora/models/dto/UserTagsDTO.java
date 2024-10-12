package com.main.Jora.models.dto;

import com.main.Jora.enums.Role;
import com.main.Jora.models.Tag;
import com.main.Jora.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTagsDTO {
    private User user;
    private List<Tag> tags;
    private Role role;
}

