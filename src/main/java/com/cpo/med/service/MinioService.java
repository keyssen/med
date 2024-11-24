package com.cpo.med.service;

import com.cpo.med.configuration.properties.MinioProperties;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.UUID;

import static com.cpo.med.utils.Constants.FILE_KEY_FORMAT;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {
    private final MinioProperties minioProperties;
    private final MinioClient minioClient;


    public String getImageUrl(UUID profileId, UUID imageId) {
        String objectKey = String.format(FILE_KEY_FORMAT, profileId, imageId);
        GetPresignedObjectUrlArgs urlArgs = GetPresignedObjectUrlArgs.builder()
                .bucket(minioProperties.getBucketName())
                .object(objectKey)
                .method(Method.GET)
                .build();
        try {
            return minioClient.getPresignedObjectUrl(urlArgs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String addFile(UUID profileId, UUID imageId, MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("Please select a file to upload.");
        }

        final byte[] fileContent;
        try {
            fileContent = file.getBytes();
            String objectKey = String.format(FILE_KEY_FORMAT, profileId, imageId);
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(fileContent)) {

                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(minioProperties.getBucketName())
                        .object(objectKey)
                        .stream(inputStream, file.getSize(), -1)
                        .build());
            }
            return objectKey;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteFile(UUID profileId, UUID imageId) {
        String objectKey = String.format(FILE_KEY_FORMAT, profileId, imageId);
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(minioProperties.getBucketName()).object(objectKey).build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}