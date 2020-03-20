package mops.persistence;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import mops.exception.MopsException;
import org.junit.jupiter.api.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Random;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers(disabledWithoutDocker = true)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileRepositoryTest {

    final Random random = new Random();
    FileRepository fileRepository;
    GenericContainer<?> minioServer;

    @BeforeAll
    void setup() throws StorageException {
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
                .waitingFor(
                        Wait
                                .forHttp("/minio/health/ready")
                                .forPort(9000)
                                .withStartupTimeout(Duration.ofSeconds(30))
                );
        minioServer.start();

        int mappedPort = minioServer.getFirstMappedPort();
        fileRepoConfig.setPort(mappedPort);

        fileRepository = new FileRepository(fileRepoConfig);
    }

    @AfterEach
    void cleanUpEach() throws StorageException {
        fileRepository.clearBucket();
    }

    @AfterAll
    void cleanUp() {
        minioServer.stop();
    }

    @Test
    void shouldSaveAFile() throws StorageException {
        long fileId = 1;
        MultipartFile file = getRandomMultipartFile();

        boolean preExistResult = fileRepository.fileExist(fileId);
        assertThat(preExistResult).isFalse();

        fileRepository.saveFile(file, fileId);

        boolean postExistResult = fileRepository.fileExist(fileId);
        assertThat(postExistResult).isTrue();
    }

    @Test
    void fileGetsDeleted() throws StorageException {
        long fileId = 1;
        MultipartFile file = getRandomMultipartFile();

        boolean preExistResult = fileRepository.fileExist(fileId);
        assertThat(preExistResult).isFalse();

        fileRepository.saveFile(file, fileId);

        boolean postExistResult = fileRepository.fileExist(fileId);
        assertThat(postExistResult).isTrue();

        fileRepository.deleteFile(fileId);
        boolean existResult = fileRepository.fileExist(fileId);

        assertThat(existResult).isFalse();
    }

    @Test
    void deleteShouldOnlyDeleteOneFile() throws StorageException {
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
    @SuppressFBWarnings(value = "RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE",
            justification = "There is no null-check here")
    void shouldReturnOriginalContent() throws StorageException, IOException {
        long fileId = 1;
        byte[] originalContent = getRandomBytes();
        MultipartFile file = new MockMultipartFile("file.bin", originalContent);

        fileRepository.saveFile(file, fileId);
        byte[] retrievedData;
        try (InputStream stream = fileRepository.getFileContent(fileId)) {
            retrievedData = stream.readAllBytes();
        }

        assertThat(retrievedData).isEqualTo(originalContent);
    }

    @Test
    void shouldListAllFiles() throws MopsException {
        long fileId1 = 1;
        long fileId2 = 2;
        long fileId3 = 3;

        MultipartFile file1 = getRandomMultipartFile();
        MultipartFile file2 = getRandomMultipartFile();
        MultipartFile file3 = getRandomMultipartFile();

        fileRepository.saveFile(file1, fileId1);
        fileRepository.saveFile(file2, fileId2);
        fileRepository.saveFile(file3, fileId3);

        Set<Long> fetchedIds = fileRepository.getAllIds();

        assertThat(fetchedIds).containsExactlyInAnyOrder(
                fileId1,
                fileId2,
                fileId3
        );

        fileRepository.deleteFile(fileId2);
        fetchedIds = fileRepository.getAllIds();

        assertThat(fetchedIds).containsExactlyInAnyOrder(
                fileId1,
                fileId3
        );
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
}
