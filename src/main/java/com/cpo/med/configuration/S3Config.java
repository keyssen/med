package com.cpo.med.configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.cpo.med.configuration.properties.S3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
@RequiredArgsConstructor
public class S3Config {

    private final S3Properties s3Properties;

    @Bean
    public AmazonS3 amazonS3() {
        return AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(
                        new com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration(
                                s3Properties.getServiceEndpoint(),
                                s3Properties.getSigningRegion()
                        )
                )
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(s3Properties.getAccessKeyId(), s3Properties.getSecretAccessKey())))
                .build();
    }
    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .endpointOverride(URI.create(s3Properties.getServiceEndpoint())) // Указание кастомного эндпоинта
                .region(Region.of(s3Properties.getSigningRegion())) // Регион
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(
                                s3Properties.getAccessKeyId(),
                                s3Properties.getSecretAccessKey()
                        )
                ))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true) // Включить поддержку Path-Style Access, если требуется
                        .build())
                .build();
    }
}
