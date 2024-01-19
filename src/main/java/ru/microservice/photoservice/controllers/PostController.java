package ru.microservice.photoservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.microservice.photoservice.entities.Photo;
import ru.microservice.photoservice.entities.Post;
import ru.microservice.photoservice.service.PhotosService;
import ru.microservice.photoservice.service.PostService;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/posts")
public class PostController {
    private final PostService postService;
    private final PhotosService photosService;

    public PostController(PostService postService, PhotosService photosService) {
        this.postService = postService;
        this.photosService = photosService;
    }

    @Operation(summary = "Создание поста")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Post createPost(@RequestPart("photos") MultipartFile[] photos, @RequestPart("post") Post posts)
            throws IOException {
        List<Photo> photosList = photosService.uploadPhotos(photos);
        return postService.createPost(posts, photosList);
    }

    @Operation(summary = "Получение поста")
    @GetMapping(path ="/{uuid}")
    public Post getPost(@PathVariable UUID uuid) {
        return postService.getPost(uuid);
    }

    @Operation(summary = "Обновление поста")
    @PutMapping(path = "/{uuid}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Post updatePost(@RequestPart("post") Post post, @PathVariable UUID uuid,
                           @RequestPart("photos") MultipartFile[] photos) throws IOException {
        List<Photo> photoslist = postService.getPost(uuid).getPhotos();
        photoslist.forEach(x -> photosService.deletePhoto(x.getName()));
        photoslist = photosService.uploadPhotos(photos);
        return postService.updatePost(post, uuid, photoslist);
    }
    @Operation(summary = "Удаление поста")
    @DeleteMapping("/{uuid}")
    public String deletePost(@PathVariable UUID uuid) {
        List<Photo> photos = postService.getPost(uuid).getPhotos();
        photos.forEach(x -> photosService.deletePhoto(x.getName()));
        return postService.deletePost(uuid);
    }
    @Operation(summary = "Получение фотографии в посте")
    @GetMapping("/{post_uuid}/photos/{photo_uuid}")
    public Photo getPhoto(@PathVariable UUID photo_uuid, @PathVariable UUID post_uuid) {
        return postService.getPhoto(post_uuid,photo_uuid);
    }
    @Operation(summary = "Удаление фотографии в посте")
    @DeleteMapping("/{post_uuid}/photos/{photo_uuid}")
    public String deletePhoto(@PathVariable UUID photo_uuid, @PathVariable UUID post_uuid) {
        return postService.deletePhoto(post_uuid,photo_uuid);
    }
}
