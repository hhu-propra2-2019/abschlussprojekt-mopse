package mops;

import mops.businesslogic.DirectoryService;
import mops.businesslogic.FileService;
import mops.businesslogic.GroupService;
import mops.persistence.FileRepository;
import mops.persistence.FileRepositoryConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Random;

@SpringTestContext
@Testcontainers
public class FileRepoTests {
    private static FileRepository fileRepository;

    @SuppressWarnings("rawtypes")
    private static GenericContainer minioServer;

    @MockBean
    private static FileRepositoryConfig fileRepoConfig;


    @BeforeAll
    static void setUp() {
        fileRepoConfig = new FileRepositoryConfig();
        fileRepoConfig.setAccessKey("access_key");
        fileRepoConfig.setSecretKey("secret_key");
        fileRepoConfig.setHost("http://localhost");
        fileRepoConfig.setBucketName("test-bucket");

        minioServer = new GenericContainer("minio/minio")
                .withEnv("MINIO_ACCESS_KEY", fileRepoConfig.getAccessKey())
                .withEnv("MINIO_SECRET_KEY", fileRepoConfig.getSecretKey())
                .withCommand("server /data")
                .withExposedPorts(9000)
                .waitingFor(new HttpWaitStrategy()
                        .forPath("/minio/health/ready")
                        .forPort(9000)
                        .withStartupTimeout(Duration.ofSeconds(10)));
        minioServer.start();

        Integer mappedPort = minioServer.getFirstMappedPort();
        fileRepoConfig.setPort(mappedPort);

        fileRepository = new FileRepository(fileRepoConfig);
    }

    @AfterAll
    static void cleanUp() {
        minioServer.stop();
    }

    @Test
    public void testRepo() {
        final int FILE_LENGTH = 4857;
        final Long FILE_ID = 7L;
        byte[] content = new byte[FILE_LENGTH];
        new Random().nextBytes(content);
        MultipartFile file = new MockMultipartFile("somefile.bin", content);

        boolean result = fileRepository.saveFile(file, FILE_ID);
        assertThat(result).isTrue();


        InputStream retrievedFile = null;
        retrievedFile = fileRepository.getFileContent(FILE_ID);
        assertThat(retrievedFile).isNotNull();

        int file_length = 0;
        try {
            file_length = retrievedFile.readAllBytes().length;
        } catch (IOException e) {

            e.printStackTrace();
        }
        assertThat(file_length).isEqualTo(FILE_LENGTH);

        result = fileRepository.deleteFile(FILE_ID);
        assertThat(result).isTrue();
    }

}
