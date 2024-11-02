package com.main.Jora.notifications;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.main.Jora.models.User;
import jakarta.persistence.*;
import lombok.Data;

import jakarta.persistence.JoinColumn;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
public class UserNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "notification_id")
    private Notification notification;

    private boolean is_read;
}

