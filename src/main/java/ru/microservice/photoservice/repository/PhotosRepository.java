package ru.microservice.photoservice.repository;


import com.amazonaws.services.s3.AmazonS3;
import org.springframework.stereotype.Component;
import ru.microservice.photoservice.properties.S3Properties;

@Component
public class PhotosRepository extends S3Repository {
    public PhotosRepository(AmazonS3 client, S3Properties properties) {
        super(client, properties.getBucketPhotos());
    }
}
