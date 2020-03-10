package mops;

import mops.persistence.FileRepository;
import mops.persistence.FileRepositoryConfig;
import mops.persistence.StorageException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.Random;

@SpringTestContext
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FileRepoTests {
    private FileRepository fileRepository;

    @SuppressWarnings("rawtypes")
    private GenericContainer minioServer;

    @MockBean
    private FileRepositoryConfig fileRepoConfig;

    @BeforeAll
    void setUp() throws StorageException {
        fileRepoConfig = new FileRepositoryConfig();
        fileRepoConfig.setAccessKey("access_key");
        fileRepoConfig.setSecretKey("secret_key");
        fileRepoConfig.setHost("http://localhost");
        fileRepoConfig.setBucketName("test-bucket");

        minioServer = new GenericContainer<>("minio/minio")
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
    void cleanUp() {
        minioServer.stop();
    }

    private byte[] getRandomBytes() {
        Random random = new Random();
        int fileLength = random.nextInt(10000) + 1;
        byte[] bytes = new byte[fileLength];
        random.nextBytes(bytes);
        return bytes;
    }

    private MultipartFile getRandomMultipartFile() {
        return new MockMultipartFile("file.bin",
                getRandomBytes()
        );
    }

    private Long getRandomId() {
        return new Random().nextLong();
    }

    @Test
    public void shouldSaveAFile() throws StorageException {
        Long fileId =  getRandomId();
        MultipartFile file = getRandomMultipartFile();

        // File shouldn't already exist
        assertThat(fileRepository.fileExist(fileId)).isFalse();

        fileRepository.saveFile(file, fileId);
        boolean existResult = fileRepository.fileExist(fileId);

        assertThat(existResult).isTrue();
    }

    @Test
    public void fileGetsDeleted() throws StorageException {
        Long fileId = getRandomId();
        MultipartFile file = getRandomMultipartFile();

        assertThat(fileRepository.fileExist(fileId)).isFalse();

        fileRepository.saveFile(file, fileId);
        assertThat(fileRepository.fileExist(fileId)).isTrue();

        boolean deleteResult = fileRepository.deleteFile(fileId);
        boolean existResult = fileRepository.fileExist(fileId);

        assertThat(deleteResult).isTrue();
        assertThat(existResult).isFalse();
    }

    @Test
    public void deleteShouldOnlyDeleteOneFile() throws StorageException {
        Long fileId1 = getRandomId();
        Long fileId2;

        // Ensure unique ID's
        do {
            fileId2 = getRandomId();
        } while (fileId1.equals(fileId2));

        MultipartFile file1 = getRandomMultipartFile();
        MultipartFile file2 = getRandomMultipartFile();

        fileRepository.saveFile(file1, fileId1);
        fileRepository.saveFile(file2, fileId2);

        fileRepository.deleteFile(fileId1);
        boolean file2StillExists = fileRepository.fileExist(fileId2);

        assertThat(file2StillExists).isTrue();
    }

    @Test
    public void shouldReturnOriginalContent() throws StorageException {
        Long fileId = getRandomId();
        byte[] originalContent = getRandomBytes();
        MultipartFile file = new MockMultipartFile("file.bin", originalContent);

        fileRepository.saveFile(file, fileId);
        byte[] retrievedData = fileRepository.getFileContent(fileId);

        assertThat(originalContent).isEqualTo(retrievedData);
    }

}
