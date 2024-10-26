package com.main.Jora.comments;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
//Пользователя не получится передать из-за глубины json-запроса
//Поэтому нужен другой трансфер-объект
@Data
@AllArgsConstructor
public class CommentReader {
    private String username;
    private String email;
    private LocalDateTime readAt;
}
