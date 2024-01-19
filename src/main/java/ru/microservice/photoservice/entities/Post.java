package ru.microservice.photoservice.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "posts")
@Data
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID uuid;

    @Column(name = "title")
    private String title;

    @Column(name = "user_uuid")
    private UUID userUuid;

    @Column(name = "descriptions")
    private String descriptions;

    @OneToMany(mappedBy="post", cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonManagedReference
    private List<Photo> photos;

}
