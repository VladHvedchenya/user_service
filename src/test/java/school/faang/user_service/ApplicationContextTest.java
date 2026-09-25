package school.faang.user_service;

import org.testcontainers.containers.GenericContainer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
class ApplicationContextTest {

    @Container
    private static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:13.6");

    @Container
    private static final GenericContainer<?> REDIS_CONTAINER =
            new GenericContainer<>(DockerImageName.parse("redis/redis-stack:latest"))
                    .withExposedPorts(6379);

    @Container
    static final MinIOContainer MINIO_CONTAINER = new MinIOContainer(
            DockerImageName.parse("minio/minio:RELEASE.2023-09-04T19-57-37Z")
    )
            .withUserName("user")
            .withPassword("password");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);

        registry.add("s3.endpoint", MINIO_CONTAINER::getS3URL);
        registry.add("s3.accessKey", MINIO_CONTAINER::getUserName);
        registry.add("s3.secretKey", MINIO_CONTAINER::getPassword);
        registry.add("s3.region", () -> "us-east-1");
    }

    @Test
    void contextLoads() {
    }
}