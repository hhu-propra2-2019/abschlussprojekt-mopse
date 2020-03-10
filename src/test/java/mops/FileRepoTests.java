package mops;

import mops.persistence.FileRepository;
import mops.persistence.FileRepositoryConfig;
import mops.persistence.StorageException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FileRepoTests {

    private FileRepository fileRepository;
    private GenericContainer<?> minioServer;
    private final Random random = new Random();

    @BeforeAll
    void setUp() throws StorageException {
        FileRepositoryConfig fileRepoConfig = new FileRepositoryConfig();
        fileRepoConfig.setAccessKey("access_key");
        fileRepoConfig.setSecretKey("secret_key");
        fileRepoConfig.setHost("http://localhost");
        fileRepoConfig.setBucketName("test-bucket");

        minioServer = new GenericContainer<>("minio/minio:latest")
                .withEnv("MINIO_ACCESS_KEY", fileRepoConfig.getAccessKey())
                .withEnv("MINIO_SECRET_KEY", fileRepoConfig.getSecretKey())
                .withCommand("server /data")
                .withExposedPorts(9000)
                .waitingFor(Wait
                        .forHttp("/minio/health/ready")
                        .forPort(9000)
                        .withStartupTimeout(Duration.ofSeconds(10)));
        minioServer.start();

        int mappedPort = minioServer.getFirstMappedPort();
        fileRepoConfig.setPort(mappedPort);

        fileRepository = new FileRepository(fileRepoConfig);
    }

    @AfterAll
    void cleanUp() {
        minioServer.stop();
    }

    private byte[] getRandomBytes() {
        int fileLength = random.nextInt(10000) + 1;
        byte[] bytes = new byte[fileLength];
        random.nextBytes(bytes);
        return bytes;
    }

    private MultipartFile getRandomMultipartFile() {
        return new MockMultipartFile("file.bin", getRandomBytes());
    }

    @Test
    public void shouldSaveAFile() throws StorageException {
        long fileId = 1;
        MultipartFile file = getRandomMultipartFile();

        boolean preExistResult = fileRepository.fileExist(fileId);
        assertThat(preExistResult).isFalse();

        fileRepository.saveFile(file, fileId);

        boolean postExistResult = fileRepository.fileExist(fileId);
        assertThat(postExistResult).isTrue();
    }

    @Test
    public void fileGetsDeleted() throws StorageException {
        long fileId = 1;
        MultipartFile file = getRandomMultipartFile();

        boolean preExistResult = fileRepository.fileExist(fileId);
        assertThat(preExistResult).isFalse();

        fileRepository.saveFile(file, fileId);

        boolean postExistResult = fileRepository.fileExist(fileId);
        assertThat(postExistResult).isTrue();

        boolean deleteResult = fileRepository.deleteFile(fileId);
        boolean existResult = fileRepository.fileExist(fileId);

        assertThat(deleteResult).isTrue();
        assertThat(existResult).isFalse();
    }

    @Test
    public void deleteShouldOnlyDeleteOneFile() throws StorageException {
        long fileId1 = 1;
        long fileId2 = 2;

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
        long fileId = 1;
        byte[] originalContent = getRandomBytes();
        MultipartFile file = new MockMultipartFile("file.bin", originalContent);

        fileRepository.saveFile(file, fileId);
        byte[] retrievedData = fileRepository.getFileContent(fileId);

        assertThat(retrievedData).isEqualTo(originalContent);
    }

}
