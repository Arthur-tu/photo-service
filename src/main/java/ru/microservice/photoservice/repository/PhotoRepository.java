package ru.microservice.photoservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.microservice.photoservice.entities.Photo;

import java.util.UUID;

public interface PhotoRepository extends JpaRepository<Photo, UUID> {
}
