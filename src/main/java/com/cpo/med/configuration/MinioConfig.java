package com.cpo.med.configuration;

import com.cpo.med.configuration.properties.MinioProperties;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@RequiredArgsConstructor
public class MinioConfig {

    private final MinioProperties minioProperties;

    @Bean
    public MinioClient minioClient() {
        try {
            MinioClient minioClient = MinioClient.builder()
                    .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                    .endpoint(minioProperties.getEndpoint())
                    .build();

            String bucketName = minioProperties.getBucketName();
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }

            return minioClient;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}