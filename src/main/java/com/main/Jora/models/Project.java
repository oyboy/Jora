package com.main.Jora.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    //Является второстепенной ссылкой
      @ManyToMany(mappedBy = "projects")
      private Set<User> users = new HashSet<>();
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "project")
    private List<Task> taskList = new ArrayList<>();
    @NotNull
    @Column(unique = true)
    private String hash = this.generateHash();
    public String generateHash() {
        String input = id + title + createdAt.getTime();
        return generateMD5Hash(input);
    }
    private String generateMD5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder hashString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hashString.append('0');
                hashString.append(hex);
            }
            return hashString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Project)) return false;
        Project project = (Project) o;
        return Objects.equals(id, project.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }
    /*Проблема
Когда Hibernate пытается получить хеш-коды для коллекций, которые являются взаимозависимыми, это может привести к бесконечному циклу вызовов hashCode(), который реализован в PersistentSet. Это происходит из-за того, что когда вы пытаетесь вызвать hashCode() одного объекта, он пытается вычислить хеш-код своих коллекций, которые ссылаются на другие объекты, и этот процесс повторяется бесконечно.*/
}
