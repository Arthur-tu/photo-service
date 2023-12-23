package ru.microservice.photoservice.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.microservice.photoservice.service.PhotosService;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/photos")
public class PhotosController {
    private final PhotosService photosService;

    @PostMapping("/upload")
    public String uploadPhoto(@RequestParam("image") MultipartFile photo) throws IOException {
        String photoFileName = photo.getOriginalFilename();
        log.info("S3 загрузило фото {}", photoFileName);
        return photosService.uploadPhoto(photoFileName, photo);
    }

    @DeleteMapping("/delete")
    public String deletePhoto(@RequestParam("fileName") String fileName) throws IOException {
        log.info("S3 удалило фото {}", fileName);
        return photosService.deletePhoto(fileName);
    }

}