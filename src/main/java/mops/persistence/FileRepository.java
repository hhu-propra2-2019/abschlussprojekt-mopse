package mops.persistence;

import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.MinioException;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import mops.persistence.config.FileRepositoryConfig;
import mops.persistence.exception.StorageException;
import mops.util.AggregateBuilder;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

/**
 * MinIO connection.
 */
@Slf4j
@Repository
@AggregateBuilder
public class FileRepository {

    /**
     * The MinIO client.
     */
    private final transient MinioClient minioClient;

    /**
     * Injected MinIO configuration.
     */
    private final transient FileRepositoryConfig configuration;

    /**
     * Connects to MinIO Server and checks if the bucket exists.
     *
     * @param configuration the injected Config.
     * @throws StorageException on Error
     */
    public FileRepository(FileRepositoryConfig configuration) throws StorageException {
        this.configuration = configuration;
        this.minioClient = MinioClient.builder()
                .endpoint(configuration.getHost() + ":" + configuration.getPort())
                .credentials(configuration.getAccessKey(), configuration.getSecretKey())
                .build();

        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(configuration.getBucketName())
                    .build())) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(configuration.getBucketName())
                        .build());
            }
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            log.error("Failed to find and create the bucket '{}':", configuration.getBucketName(), e);
            throw new StorageException("Fehler beim Suchen und Erstellen des Buckets.", e);
        }
    }

    /**
     * Saves a file.
     *
     * @param file   the file that should get saved permanently.
     * @param fileId the File ID given by the FileInfo database.
     * @throws StorageException on Error
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    public void saveFile(MultipartFile file, long fileId) throws StorageException {
        try (InputStream stream = file.getInputStream()) {
            saveFile(stream, file.getSize(), file.getContentType(), fileId);
        } catch (IOException | StorageException e) {
            log.error("Failed so save file '{}' to MinIO server:", file.getName(), e);
            throw new StorageException("Fehler beim Speichern der Datei.", e);
        }
    }

    /**
     * Saves an input stream with meta information.
     *
     * @param stream input stream (must be closed by caller)
     * @param size   size in bytes
     * @param type   content type
     * @param fileId the File ID given by the FileInfo database.
     * @throws StorageException on Error
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    public void saveFile(InputStream stream, long size, String type, long fileId) throws StorageException {
        try {
            PutObjectArgs.Builder builder = PutObjectArgs.builder()
                    .bucket(configuration.getBucketName())
                    .object(String.valueOf(fileId))
                    .stream(stream, size, -1);
            if (type != null && !type.isEmpty()) {
                builder.contentType(type);
            }

            minioClient.putObject(builder.build());
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            log.error("Failed so save file '{}' to MinIO server:", fileId, e);
            throw new StorageException("Fehler beim Speichern der Datei.", e);
        }
    }

    /**
     * Deletes a file permanently.
     *
     * @param fileId the ID of the file that's desired to be deleted.
     * @throws StorageException on error
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    public void deleteFile(long fileId) throws StorageException {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(configuration.getBucketName())
                    .object(String.valueOf(fileId))
                    .build());
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            log.error("Failed to delete file with id {} from MinIO Server:", fileId, e);
            throw new StorageException("Fehler beim Löschen der Datei.", e);
        }
    }

    /**
     * Retrieves the bytes of the file.
     *
     * @param fileId the ID of the file that's desired to be returned.
     * @return file content as byte array
     * @throws StorageException on error
     */
    @SuppressWarnings({ "PMD.DataflowAnomalyAnalysis", "PMD.LawOfDemeter" })
    public InputStream getFileContent(long fileId) throws StorageException {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(configuration.getBucketName())
                    .object(String.valueOf(fileId))
                    .build());
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            log.error("Failed to get content of file with id {}:", fileId, e);
            throw new StorageException("Fehler beim Zugriff auf den Inhalt der Datei.", e);
        }
    }

    /**
     * Checks if a file with a specified ID already exists.
     *
     * @param fileId the file ID
     * @return true if found
     */
    @SuppressWarnings({ "PMD.DataflowAnomalyAnalysis", "PMD.OnlyOneReturn", "PMD.LawOfDemeter" })
    public boolean fileExist(long fileId) throws StorageException {
        StatObjectResponse objectStat;
        try {
            objectStat = minioClient.statObject(StatObjectArgs.builder()
                    .bucket(configuration.getBucketName())
                    .object(String.valueOf(fileId))
                    .build());
        } catch (ErrorResponseException e) {
            // file not found
            return false;
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            log.error("Failed to check file existence for id {}:", fileId, e);
            throw new StorageException("Fehler beim Zugriff auf Datei.", e);
        }

        return objectStat != null;
    }

    /**
     * Fetches all IDs.
     *
     * @return all File IDs
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    public Set<Long> getAllIds() throws StorageException {
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder()
                    .bucket(configuration.getBucketName())
                    .build());
            Set<Long> ids = new HashSet<>();
            for (Result<Item> item : results) {
                ids.add(
                        Long.parseLong(item.get().objectName())
                );
            }
            return ids;
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            throw new StorageException("Fehler beim Laden aller File IDs.", e);
        }
    }

    /**
     * Removes all files. For internal use only.
     *
     * @throws StorageException if an error occurs
     */
    @SuppressWarnings({ "PMD.DefaultPackage", "PMD.LawOfDemeter" })
    void clearBucket() throws StorageException {
        try {
            for (Result<Item> result : minioClient.listObjects(ListObjectsArgs.builder()
                    .bucket(configuration.getBucketName())
                    .build())) {
                minioClient.removeObject(RemoveObjectArgs.builder()
                        .bucket(configuration.getBucketName())
                        .object(result.get().objectName())
                        .build());
            }
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            log.error("Failed to clear bucket:", e);
            throw new StorageException("Bucket konnte nicht geleert werden.", e);
        }
    }
}
