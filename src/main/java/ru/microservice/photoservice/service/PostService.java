package ru.microservice.photoservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.microservice.photoservice.entities.Photo;
import ru.microservice.photoservice.entities.Post;
import ru.microservice.photoservice.repository.PhotoRepository;
import ru.microservice.photoservice.repository.PostRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PhotoRepository photoRepository;

    public Post createPost(Post post, List<Photo> photos) {
        post = postRepository.save(post);
        List<Photo> photosList = new ArrayList<>();
        for (Photo photo : photos) {
              photo.setPost(post);
              photo = photoRepository.save(photo);
              photosList.add(photo);
        }
        post.setPhotos(photosList);
        return post;
    }

    public Post getPost(UUID uuid) {
        return postRepository.findById(uuid).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Post updatePost(Post post, UUID id, List<Photo> photoslist) {
        if (postRepository.existsById(id)) {
            Optional<Post> oldPostOptional = postRepository.findById(id);
            Post oldpost = oldPostOptional.get();
            oldpost.setTitle(post.getTitle());
            oldpost.setDescriptions(post.getDescriptions());
            oldpost.setUserUuid(post.getUserUuid());
            oldpost.getPhotos().clear();
            List<Photo> savedPhotos = new ArrayList<>();
            for (Photo photo : photoslist) {
                photo.setPost(oldpost);
                savedPhotos.add(photo);
            }
            oldpost.getPhotos().addAll(savedPhotos);
            return postRepository.save(oldpost);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    public String deletePost(UUID id) {
        if (postRepository.existsById(id)) {
            postRepository.deleteById(id);
            System.out.println("Пост удален");
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return String.format("Пост с id= %s успешно удален", id);
    }

    public Photo getPhoto(UUID postUuid, UUID photoUuid) {
        if (postRepository.existsById(postUuid)) {
            return photoRepository.findById(photoUuid).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND));
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    public String deletePhoto(UUID postUuid, UUID photoUuid) {
        if (postRepository.existsById(postUuid)) {
            if (postRepository.existsById(photoUuid)) {
                photoRepository.deleteById(photoUuid);
                return String.format("Фотография с id=%s успешно удалена из поста с id=%s", photoUuid, postUuid);
            } else {
                throw new  ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
