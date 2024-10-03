package com.main.Jora.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty
    @Size(max=50, message="title must be -le 50 symb")
    private String title;

    @Size(max=255, message="Description must be -le 255 symb")
    private String description;
    private Date createdAt = new Date();
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "project")
    private List<Task> taskList = new ArrayList<>();
}
