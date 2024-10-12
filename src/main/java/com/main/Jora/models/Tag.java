package com.main.Jora.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
@ToString
@Entity
@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Size(max = 50)
    private String name;

    @ToString.Exclude
    @ManyToMany(mappedBy = "tags")
    private List<Task> tasks = new ArrayList<>();
    @ToString.Exclude
    @ManyToMany(mappedBy = "tags")
    private List<Project> projects = new ArrayList<>();

    @ToString.Exclude
    @ManyToMany(mappedBy = "tags")
    private List<User> users = new ArrayList<>();

/*    @ToString.Exclude
    @Transient
    public static List<Tag> defaultTags = new ArrayList<>(){{
        add(new Tag("Backend"));
        add(new Tag("Frontend"));
        add(new Tag("Android"));
        add(new Tag("IOS"));
        add(new Tag("Common"));
        add(new Tag("Bug"));
        add(new Tag("Critical"));
    }};*/
}
