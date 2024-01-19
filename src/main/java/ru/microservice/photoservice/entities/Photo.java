package ru.microservice.photoservice.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "photos")
@Data
@ToString(exclude = "post")
@AllArgsConstructor
@RequiredArgsConstructor
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID uuid;

    @Column(name = "link")
    private String link;

    @Column(name = "name")
    private String name;

    @JsonBackReference
    @ManyToOne()
    @JoinColumn(name="post_id", referencedColumnName = "uuid")
    private Post post;
}
