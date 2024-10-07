package com.main.Jora.models;

import com.main.Jora.enums.Role;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class UserProjectRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;
    @Enumerated(EnumType.STRING)
    private Role role;
    private boolean banned;
}
