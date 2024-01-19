package ru.microservice.photoservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "Загрузка в minio")
    @PostMapping("/upload")
    public String uploadPhoto(@RequestParam("image") MultipartFile photo) throws IOException {
        String photoFileName = photo.getOriginalFilename();
        return photosService.uploadPhoto(photoFileName, photo);
    }
    @Operation(summary = "Удаление из minio")
    @DeleteMapping("/delete")
    public String deletePhoto(@RequestParam("fileName") String fileName) {
        return photosService.deletePhoto(fileName);
    }

}