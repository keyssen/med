package com.cpo.med;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
public interface AbstractMinioClientTest {
    String MINIO_IMAGE = "minio/minio:latest";
    String MINIO_ENDPOINT = "http://127.0.0.1:9000";
    String MINIO_ACCESS_KEY = "minioadmin";
    String MINIO_SECRET_KEY = "minioadmin";
    String MINIO_BUCKET_NAME = "test-bucket-name";
    Integer EXPOSED_PORT = 9000;

    @Container
    MinIOContainer minioContainer = new MinIOContainer(
            DockerImageName.parse(MINIO_IMAGE)
    )
            .withExposedPorts(EXPOSED_PORT)
            .withEnv("MINIO_ROOT_USER", MINIO_ACCESS_KEY)
            .withEnv("MINIO_ROOT_PASSWORD", MINIO_SECRET_KEY)
            .withCommand("server", "/data")
            .withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(
                    new HostConfig().withPortBindings(new PortBinding(Ports.Binding.bindPort(EXPOSED_PORT), new ExposedPort(EXPOSED_PORT)))));

    @BeforeAll
    static void init() {
        minioContainer.start();
    }

    @DynamicPropertySource
    static void registerDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("minio.endpoint", () -> MINIO_ENDPOINT);
        registry.add("minio.accessKey", () -> MINIO_ACCESS_KEY);
        registry.add("minio.secretKey", () -> MINIO_SECRET_KEY);
        registry.add("minio.bucket-name", () -> MINIO_BUCKET_NAME);
    }
}
