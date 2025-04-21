package com.main.Jora.controllers;

import com.main.Jora.models.UserAvatar;
import com.main.Jora.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/home/user/{userId}")
public class UserRestController {
    @Autowired
    UserService userService;
    //Вынесено в rest-контроллер, чтобы можно было обращаться к этому объекту
    //из любой точки программы, просто вызвав <img src="/home/user/${currentUser.id}/avatar" class="avatar"/>
    @GetMapping("/avatar")
    public ResponseEntity<byte[]> getAvatar(@PathVariable Long userId) {
        UserAvatar userAvatar = userService.findAvatarByUserId(userId);
        if (userAvatar != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(userAvatar.getBytes());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}