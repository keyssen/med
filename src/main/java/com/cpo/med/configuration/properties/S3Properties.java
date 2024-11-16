package com.cpo.med.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "s3")
@Getter
@Setter
public class S3Properties {
    private String accessKeyId;
    private String secretAccessKey;
    private String serviceEndpoint;
    private String signingRegion;
    private String bucket;
}