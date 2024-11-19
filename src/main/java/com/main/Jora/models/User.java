package com.main.Jora.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@EqualsAndHashCode
@ToString
@Data
public class User implements UserDetails, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max=50, message="Имя не может быть больше 50 символов")
    @Size(min=4, message="Имя должно быть больше 4 символов")
    private String username;

    @Size(min=4, message="Пароль не может быть меньше 4 символов")
    @ToString.Exclude
    private String password;
    @Transient
    private String confirmPassword;

    @Size(max = 50, message = "Почта не может быть больше 50 символов")
    //@Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,6}$", message = "Некорректный email")
    @Size(min=3, message = "Почта должна быть больше 3 символов") //Заменить на pattern
    private String email;
    private boolean active;
    //Связь пользователей с проектами
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    transient private Set<UserProjectRole> userProjectRoles = new HashSet<>();
    //Связь пользователей с задачами
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    transient private Set<UserTask> userTasks = new HashSet<>();

    //Связь пользователей с тегами
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToMany
    @JoinTable(
            name = "user_tags",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userProjectRoles.stream()
                .map(UserProjectRole::getRole)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
