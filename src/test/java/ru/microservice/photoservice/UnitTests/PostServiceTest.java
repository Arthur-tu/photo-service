package ru.microservice.photoservice.UnitTests;

import org.instancio.Instancio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;
import ru.microservice.photoservice.entities.Photo;
import ru.microservice.photoservice.entities.Post;
import ru.microservice.photoservice.repository.PhotoRepository;
import ru.microservice.photoservice.repository.PostRepository;
import ru.microservice.photoservice.service.PostService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;



class PostServiceTest {

    @Test
    void createPost() {
        PostRepository postRepository = Mockito.mock(PostRepository.class);
        PhotoRepository photoRepository = Mockito.mock(PhotoRepository.class);
        PostService postService = new PostService(postRepository, photoRepository);
        Post post = Instancio.of(Post.class).create();
        List<Photo> photos = Instancio.ofList(Photo.class).create();

        Mockito.when(postRepository.save(Mockito.any(Post.class))).thenReturn(post);
        photos.forEach(x -> Mockito.when(photoRepository.save(x)).thenReturn(x));

        Post result = postService.createPost(post, photos);
        Assertions.assertEquals(post.getUuid().toString(), result.getUuid().toString());
    }

    @Test
    void getPost() {
        Post post = Instancio.of(Post.class).create();
        PostRepository postRepository = Mockito.mock(PostRepository.class);
        PhotoRepository photoRepository = Mockito.mock(PhotoRepository.class);
        PostService postService = new PostService(postRepository, photoRepository);
        Mockito.when(postRepository.findById(post.getUuid())).thenReturn(Optional.of(post));

        String result = String.valueOf(postService.getPost(post.getUuid()));

        Assertions.assertEquals(post.toString(), result);
    }

    @Test
    void getPostNotFound() {
        Post post = Instancio.of(Post.class).create();
        PostRepository postRepository = Mockito.mock(PostRepository.class);
        PhotoRepository photoRepository = Mockito.mock(PhotoRepository.class);
        PostService postService = new PostService(postRepository, photoRepository);
        Mockito.when(postRepository.findById(ArgumentMatchers.any(UUID.class))).thenReturn(Optional.empty());

        Executable result = () -> postService.getPost(post.getUuid());

        Assertions.assertThrows(ResponseStatusException.class, result);
    }

    @Test
    void updatePost() {
        Post post = Instancio.of(Post.class).create();
        List<Photo> photos = Instancio.ofList(Photo.class).create();
        PostRepository postRepository = Mockito.mock(PostRepository.class);
        PhotoRepository photoRepository = Mockito.mock(PhotoRepository.class);
        PostService postService = new PostService(postRepository, photoRepository);

        Mockito.when(postRepository.save(ArgumentMatchers.any(Post.class))).thenReturn(post);
        Mockito.when(postRepository.existsById(ArgumentMatchers.any(UUID.class))).thenReturn(true);
        Mockito.when(postRepository.findById(ArgumentMatchers.any(UUID.class))).thenReturn(Optional.ofNullable(post));
        photos.forEach(x -> Mockito.when(photoRepository.save(x)).thenReturn(x));

        Post updatedPost = postService.updatePost(post,post.getUuid(), photos);

        Assertions.assertEquals(post.getUuid().toString(), updatedPost.getUuid().toString());
    }

    @Test
    void updatePostNotFound() {
        Post post = Instancio.of(Post.class).create();
        List<Photo> photos = Instancio.ofList(Photo.class).create();
        PostRepository postRepository = Mockito.mock(PostRepository.class);
        PhotoRepository photoRepository = Mockito.mock(PhotoRepository.class);
        PostService postService = new PostService(postRepository, photoRepository);

        Mockito.when(postRepository.save(ArgumentMatchers.any(Post.class))).thenReturn(post);
        Mockito.when(postRepository.existsById(ArgumentMatchers.any(UUID.class))).thenReturn(false);
        Mockito.when(postRepository.findById(ArgumentMatchers.any(UUID.class))).thenReturn(Optional.ofNullable(post));
        photos.forEach(x -> Mockito.when(photoRepository.save(x)).thenReturn(x));

        Executable result = () -> postService.updatePost(post,post.getUuid(), photos);

        Assertions.assertThrows(ResponseStatusException.class, result);
    }

    @Test
    void deletePost() {
        Post post = Instancio.of(Post.class).create();
        PostRepository postRepository = Mockito.mock(PostRepository.class);
        PhotoRepository photoRepository = Mockito.mock(PhotoRepository.class);
        PostService postService = new PostService(postRepository, photoRepository);
        Mockito.when(postRepository.existsById(ArgumentMatchers.any(UUID.class))).thenReturn(true);
        Mockito.when(postRepository.findById(post.getUuid())).thenReturn(Optional.of(post));

        String result = postService.deletePost(post.getUuid());

        Assertions.assertEquals(String.format("Пост с id= %s успешно удален", post.getUuid()), result);
    }

    @Test
    void deletePostNotFound() {
        Post post = Instancio.of(Post.class).create();
        PostRepository postRepository = Mockito.mock(PostRepository.class);
        PhotoRepository photoRepository = Mockito.mock(PhotoRepository.class);
        PostService postService = new PostService(postRepository, photoRepository);
        Mockito.when(postRepository.existsById(ArgumentMatchers.any(UUID.class))).thenReturn(false);

        Executable result = () -> postService.deletePost(post.getUuid());

        Assertions.assertThrows(ResponseStatusException.class, result);
    }

    @Test
    void getPhoto() {
        Post post = Instancio.of(Post.class).create();
        Photo photo = Instancio.of(Photo.class).create();
        PostRepository postRepository = Mockito.mock(PostRepository.class);
        PhotoRepository photoRepository = Mockito.mock(PhotoRepository.class);
        PostService postService = new PostService(postRepository, photoRepository);
        Mockito.when(postRepository.existsById(ArgumentMatchers.any(UUID.class))).thenReturn(true);
        Mockito.when(postRepository.findById(post.getUuid())).thenReturn(Optional.of(post));
        Mockito.when(photoRepository.findById(ArgumentMatchers.any(UUID.class))).thenReturn(Optional.of(photo));

        Photo result = postService.getPhoto(post.getUuid(), photo.getUuid());

        Assertions.assertEquals(photo.getUuid().toString(), result.getUuid().toString());
    }

    @Test
    void getPhotoPostNotFound() {
        Post post = Instancio.of(Post.class).create();
        Photo photo = Instancio.of(Photo.class).create();
        PostRepository postRepository = Mockito.mock(PostRepository.class);
        PhotoRepository photoRepository = Mockito.mock(PhotoRepository.class);
        PostService postService = new PostService(postRepository, photoRepository);
        Mockito.when(postRepository.existsById(ArgumentMatchers.any(UUID.class))).thenReturn(false);
        Mockito.when(postRepository.findById(post.getUuid())).thenReturn(Optional.of(post));
        Mockito.when(photoRepository.findById(ArgumentMatchers.any(UUID.class))).thenReturn(Optional.of(photo));

        Executable result = () -> postService.getPhoto(post.getUuid(), photo.getUuid());

        Assertions.assertThrows(ResponseStatusException.class, result);
    }

    @Test
    void getPhotoNotFound() {
        Post post = Instancio.of(Post.class).create();
        Photo photo = Instancio.of(Photo.class).create();
        PostRepository postRepository = Mockito.mock(PostRepository.class);
        PhotoRepository photoRepository = Mockito.mock(PhotoRepository.class);
        PostService postService = new PostService(postRepository, photoRepository);
        Mockito.when(postRepository.existsById(ArgumentMatchers.any(UUID.class))).thenReturn(true);
        Mockito.when(postRepository.findById(post.getUuid())).thenReturn(Optional.of(post));
        Mockito.when(photoRepository.findById(ArgumentMatchers.any(UUID.class))).thenReturn(Optional.empty());

        Executable result = () -> postService.getPhoto(post.getUuid(), photo.getUuid());

        Assertions.assertThrows(ResponseStatusException.class, result);
    }

    @Test
    void deletePhoto() {
        Post post = Instancio.of(Post.class).create();
        Photo photo = Instancio.of(Photo.class).create();
        PostRepository postRepository = Mockito.mock(PostRepository.class);
        PhotoRepository photoRepository = Mockito.mock(PhotoRepository.class);
        PostService postService = new PostService(postRepository, photoRepository);
        Mockito.when(postRepository.existsById(ArgumentMatchers.any(UUID.class))).thenReturn(true);
        Mockito.when(postRepository.findById(post.getUuid())).thenReturn(Optional.of(post));
        Mockito.when(photoRepository.findById(ArgumentMatchers.any(UUID.class))).thenReturn(Optional.of(photo));

        String result = postService.deletePhoto(post.getUuid(), photo.getUuid());

        Assertions.assertEquals(String.format("Фотография с id=%s успешно удалена из поста с id=%s",
                photo.getUuid(), post.getUuid()), result);
    }

    @Test
    void deletePhotoPostNotFound() {
        Post post = Instancio.of(Post.class).create();
        Photo photo = Instancio.of(Photo.class).create();
        PostRepository postRepository = Mockito.mock(PostRepository.class);
        PhotoRepository photoRepository = Mockito.mock(PhotoRepository.class);
        PostService postService = new PostService(postRepository, photoRepository);
        Mockito.when(postRepository.existsById(ArgumentMatchers.any(UUID.class))).thenReturn(false);
        Mockito.when(postRepository.findById(post.getUuid())).thenReturn(Optional.of(post));
        Mockito.when(photoRepository.findById(ArgumentMatchers.any(UUID.class))).thenReturn(Optional.of(photo));

        Executable result = () -> postService.getPhoto(post.getUuid(), photo.getUuid());

        Assertions.assertThrows(ResponseStatusException.class, result);
    }

    @Test
    void deletePhotoNotFound() {
        Post post = Instancio.of(Post.class).create();
        Photo photo = Instancio.of(Photo.class).create();
        PostRepository postRepository = Mockito.mock(PostRepository.class);
        PhotoRepository photoRepository = Mockito.mock(PhotoRepository.class);
        PostService postService = new PostService(postRepository, photoRepository);
        Mockito.when(postRepository.existsById(ArgumentMatchers.any(UUID.class))).thenReturn(true);
        Mockito.when(postRepository.findById(post.getUuid())).thenReturn(Optional.of(post));
        Mockito.when(photoRepository.findById(ArgumentMatchers.any(UUID.class))).thenReturn(Optional.empty());

        Executable result = () -> postService.getPhoto(post.getUuid(), photo.getUuid());

        Assertions.assertThrows(ResponseStatusException.class, result);
    }
}