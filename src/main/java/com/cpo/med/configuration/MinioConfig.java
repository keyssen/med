package com.cpo.med.configuration;

import com.cpo.med.configuration.properties.MinioProperties;
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
            return MinioClient.builder()
                    .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                    .endpoint(minioProperties.getEndpoint())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}