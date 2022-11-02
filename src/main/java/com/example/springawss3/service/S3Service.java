package com.example.springawss3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service implements FileService {

    @Value("${bucketName}")
    private String bucketName;

    private final AmazonS3 s3;

    @Override
    public void uploadFile(MultipartFile file) {
        String fileName = createFileName(Objects.requireNonNull(file.getOriginalFilename()));
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        try (InputStream inputStream = file.getInputStream()) {
            s3.putObject(new PutObjectRequest(bucketName, fileName, inputStream, objectMetadata));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String createFileName(String originalFilename) {
        String filename = originalFilename.substring(0, originalFilename.lastIndexOf("."));

        return filename
                + UUID.randomUUID().toString().concat(getFileExtension(originalFilename));
    }

    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }

    @Override
    public byte[] getObject(String filename) {
        S3Object object = s3.getObject(bucketName, filename);
        S3ObjectInputStream objectContent = object.getObjectContent();

        try {
            return IOUtils.toByteArray(objectContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String deleteFile(String filename) {
        s3.deleteObject(bucketName, filename);
        return "File deleted";
    }

    @Override
    public List<String> findAll() {
        ListObjectsV2Result listObjectsV2Result = s3.listObjectsV2(bucketName);
        return listObjectsV2Result.getObjectSummaries()
                .stream().map(S3ObjectSummary::getKey).collect(Collectors.toList());
    }

    public boolean isValid(MultipartFile file) {
        if (file.getSize() > 30000) {
            return false;
        }

        String name = Objects.requireNonNull(file.getOriginalFilename()).toUpperCase();
        return name.endsWith(".JPG") || name.endsWith(".PNG") || name.endsWith(".GIF");
    }
}
