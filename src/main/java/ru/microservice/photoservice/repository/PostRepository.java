package ru.microservice.photoservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.microservice.photoservice.entities.Post;

import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {

}
