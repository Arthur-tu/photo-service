package ru.microservice.photoservice.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.datical.liquibase.ext.checks.config.IFileAccessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import ru.microservice.photoservice.entities.Photo;
import ru.microservice.photoservice.properties.S3Properties;
import ru.microservice.photoservice.repository.PhotosRepository;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PhotosService {

    private final PhotosRepository photosRepository;
    private final S3Properties properties;

    public Collection<String> getAllPhotos() {
        return photosRepository.listKeys("/");
    }

    public String uploadPhoto(String fileName, MultipartFile file) throws IOException {
        log.info("Загрузка {} в S3 бакет {} ", fileName ,properties.getBucketPhotos());
        try (InputStream stream = new ByteArrayInputStream(file.getBytes())){
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(MediaType.MULTIPART_FORM_DATA_VALUE);
            metadata.setContentLength(file.getSize());
            photosRepository.put(fileName, stream, metadata);
        } catch (AmazonServiceException e) {
            log.error(e.getErrorMessage());
            return String.format("Фотография '%s' не загрузилась", fileName);
        }
        log.info("Фотография '{}' была успешно загружена", fileName);
        return String.format("Фотография '%s' была успешно загружена", fileName);
    }

    public List<Photo> uploadPhotos(MultipartFile[] photos) throws IOException {
        List<Photo> photoList = new ArrayList<>();
        if (photos[0].getSize() != 0) {
            for (MultipartFile photo : photos) {
                log.info("Загрузка {} в S3 бакет {} ", photo.getOriginalFilename(), properties.getBucketPhotos());
                try (InputStream stream = new ByteArrayInputStream(photo.getBytes())) {
                    ObjectMetadata metadata = new ObjectMetadata();
                    metadata.setContentType(MediaType.MULTIPART_FORM_DATA_VALUE);
                    metadata.setContentLength(photo.getSize());
                    photosRepository.put(photo.getOriginalFilename(), stream, metadata);
                    Photo p = new Photo();
                    p.setLink(photosRepository.getURL(photo.getOriginalFilename()).toString());
                    p.setName(photosRepository.get(photo.getOriginalFilename()).get().getKey());
                    photoList.add(p);
                    log.info("Фотография '{}' была успешно загружена", photo.getOriginalFilename());
                } catch (AmazonServiceException e) {
                    log.error(e.getErrorMessage());
                }
            }
        }
        return photoList;
    }

    public String deletePhoto(String fileName) {
        String bucketName = properties.getBucketPhotos();
        log.info("Удаление фотографии {} из S3 бакета {} ", fileName, bucketName);
        try {
            photosRepository.delete(fileName);
        } catch (AmazonServiceException e) {
            log.error(e.getErrorMessage());
            return String.format("Фотография '%s' не была удалена", fileName);
        }
        log.info("Фотография '{}' удалилась успешно", fileName);
        return String.format("Фотография '%s' удалилась успешно", fileName);
    }
}
