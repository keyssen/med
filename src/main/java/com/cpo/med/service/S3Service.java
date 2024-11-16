package com.cpo.med.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.cpo.med.configuration.properties.S3Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

import static com.cpo.med.utils.Constants.FILE_KEY_FORMAT;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Properties s3Properties;
    private final AmazonS3 amazonS3;
    private final S3Client s3Client;


    public String getImageUrl(UUID profileId, UUID imageId) {
        return amazonS3.getUrl(s3Properties.getBucket(), String.format(FILE_KEY_FORMAT, profileId, imageId)).toString();
    }

    public String addFile(UUID profileId, UUID imageId, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("Please select a file to upload.");
        }

        final byte[] fileContent = file.getBytes();
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(fileContent)) {
            final ObjectMetadata metadata = new ObjectMetadata();

            metadata.setContentLength(fileContent.length);

            final String fileName = file.getOriginalFilename();
            final int dotIndex = fileName.lastIndexOf('.');

            metadata.addUserMetadata("name", fileName.substring(0, dotIndex));
            metadata.addUserMetadata("extension", fileName.substring(dotIndex + 1));
            String objectKey = String.format(FILE_KEY_FORMAT, profileId, imageId);
            amazonS3.putObject(s3Properties.getBucket(), objectKey, inputStream, metadata);
            return objectKey;
        }
    }

    public void deleteFile(UUID profileId, UUID imageId) {
        try {
            String objectKey = String.format(FILE_KEY_FORMAT, profileId, imageId);
            amazonS3.deleteObject(s3Properties.getBucket(), objectKey);
            System.out.println("Объект успешно удален: " + objectKey);
        } catch (Exception e) {
            System.err.println("Ошибка при удалении объекта: " + e.getMessage());
        }
    }
}