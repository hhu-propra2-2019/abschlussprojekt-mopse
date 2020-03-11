package mops.persistence;

import io.minio.MinioClient;
import io.minio.ObjectStat;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;


@Repository
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
        try {
            this.minioClient = new MinioClient(
                    configuration.getHost(),
                    configuration.getPort(),
                    configuration.getAccessKey(),
                    configuration.getSecretKey()
            );
        } catch (InvalidEndpointException | InvalidPortException e) {
            throw new StorageException("Fehler beim Verbinden zum MinIO Server.", e);
        }

        try {
            if (!minioClient.bucketExists(configuration.getBucketName())) {
                minioClient.makeBucket(configuration.getBucketName());
            }
        } catch (InvalidBucketNameException | NoSuchAlgorithmException | InsufficientDataException
                | IOException | InvalidKeyException | NoResponseException | XmlPullParserException
                | ErrorResponseException | InternalException | InvalidResponseException | RegionConflictException e) {
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
    public void saveFile(MultipartFile file, long fileId) throws StorageException {
        try (InputStream stream = file.getInputStream()) {
            minioClient.putObject(configuration.getBucketName(),
                    String.valueOf(fileId),
                    stream,
                    file.getSize(),
                    new HashMap<>(),
                    null, // no encryption will be needed
                    file.getContentType()
            );
        } catch (MinioException | IOException | InvalidKeyException
                | NoSuchAlgorithmException | XmlPullParserException e) {
            throw new StorageException("Fehler beim Speichern der Datei.", e);
        }
    }

    /**
     * Deletes a file permanently.
     *
     * @param fileId the ID of the file that's desired to be deleted.
     * @throws StorageException on error
     */
    public void deleteFile(long fileId) throws StorageException {
        try {
            minioClient.removeObject(
                    configuration.getBucketName(),
                    String.valueOf(fileId)
            );
        } catch (InvalidBucketNameException | NoSuchAlgorithmException | InsufficientDataException
                | IOException | InvalidKeyException | NoResponseException | XmlPullParserException
                | ErrorResponseException | InternalException | InvalidArgumentException | InvalidResponseException e) {
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
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public InputStream getFileContent(long fileId) throws StorageException {
        try {
            return minioClient.getObject(
                    configuration.getBucketName(),
                    String.valueOf(fileId)
            );
        } catch (IOException | InvalidBucketNameException | NoSuchAlgorithmException | InsufficientDataException
                | InvalidKeyException | NoResponseException | XmlPullParserException | ErrorResponseException
                | InternalException | InvalidArgumentException | InvalidResponseException e) {
            throw new StorageException("Fehler beim Zugriff auf den Inhalt der Datei.", e);
        }
    }

    /**
     * Checks if a file with a specified ID already exists.
     *
     * @param fileId the file ID
     * @return true if found
     */
    @SuppressWarnings({ "PMD.DataflowAnomalyAnalysis", "PMD.OnlyOneReturn" })
    public boolean fileExist(long fileId) throws StorageException {
        ObjectStat objectStat;
        try {
            objectStat = minioClient.statObject(
                    configuration.getBucketName(),
                    String.valueOf(fileId)
            );
        } catch (ErrorResponseException e) {
            // not found
            return false;
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoResponseException | InvalidResponseException
                | XmlPullParserException | InvalidArgumentException | InsufficientDataException | InternalException
                | InvalidBucketNameException | IOException e) {
            throw new StorageException("Fehler beim Zugriff auf Datei.", e);
        }

        return objectStat != null;
    }

    /**
     * Removes all files. For internal use only.
     *
     * @throws StorageException if an error occurs
     */
    @SuppressWarnings("PMD")
    void clearBucket() throws StorageException {
        try {
            for (Result<Item> result : minioClient.listObjects(configuration.getBucketName())) {
                minioClient.removeObject(configuration.getBucketName(), result.get().objectName());
            }
        } catch (Exception e) {
            throw new StorageException("Bucket konnte nicht geleert werden.", e);
        }
    }
}
