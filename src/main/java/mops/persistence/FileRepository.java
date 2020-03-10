package mops.persistence;

import io.minio.MinioClient;
import io.minio.ObjectStat;
import io.minio.errors.*;
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
     * @param configuration the injected Conf.
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
            throw new StorageException(e);
        }

        try {
            if (!minioClient.bucketExists(configuration.getBucketName())) {
                minioClient.makeBucket(configuration.getBucketName());
            }
        } catch (InvalidBucketNameException | NoSuchAlgorithmException | InsufficientDataException
                | IOException | InvalidKeyException | NoResponseException | XmlPullParserException
                | ErrorResponseException | InternalException | InvalidResponseException | RegionConflictException e) {
            throw new StorageException(e);
        }
    }

    /**
     * Saves a file.
     *
     * @param file   the file that should get saved permanently.
     * @param fileId the File ID given by the FileInfo database.
     * @throws StorageException on Error
     */
    public void saveFile(MultipartFile file, Long fileId) throws StorageException {
        try (InputStream stream = file.getInputStream()) {
            minioClient.putObject(configuration.getBucketName(),
                    fileId.toString(),
                    stream,
                    file.getSize(),
                    new HashMap<>(),
                    null, // no encryption will be needed
                    file.getContentType()
            );
        } catch (MinioException | IOException | InvalidKeyException
                | NoSuchAlgorithmException | XmlPullParserException e) {
            throw new StorageException(e);
        }
    }

    /**
     * Deletes a file permanently.
     *
     * @param fileId the ID of the file that's desired to be deleted.
     * @return true if successful; false if not.
     * @throws StorageException on error
     */
    public boolean deleteFile(Long fileId) throws StorageException {
        try {
            minioClient.removeObject(
                    configuration.getBucketName(),
                    fileId.toString()
            );
        } catch (InvalidBucketNameException | NoSuchAlgorithmException | InsufficientDataException
                | IOException | InvalidKeyException | NoResponseException | XmlPullParserException
                | ErrorResponseException | InternalException | InvalidArgumentException | InvalidResponseException e) {
            throw new StorageException(e);
        }

        return !fileExist(fileId);
    }

    /**
     * Retrieves the bytes of the file.
     *
     * @param fileId the ID of the file that's desired to be returned.
     * @return file content as byte array
     * @throws StorageException on error
     */
    @SuppressWarnings({ "PMD.DataflowAnomalyAnalysis", "PMD.LawOfDemeter", "PMD.CloseResource" })
    public byte[] getFileContent(Long fileId) throws StorageException {
        byte[] content;
        try (InputStream stream = minioClient.getObject(
                configuration.getBucketName(),
                fileId.toString()
        )) {
            content = stream.readAllBytes();
        } catch (IOException | InvalidBucketNameException | NoSuchAlgorithmException | InsufficientDataException
                | InvalidKeyException | NoResponseException | XmlPullParserException | ErrorResponseException
                | InternalException | InvalidArgumentException | InvalidResponseException e) {
            throw new StorageException(e);
        }
        return content;
    }

    /**
     * Checks if a file with a specified ID already exists.
     *
     * @param fileId the file ID
     * @return true if found
     */
    @SuppressWarnings({ "PMD.DataflowAnomalyAnalysis", "PMD.OnlyOneReturn" })
    public boolean fileExist(Long fileId) throws StorageException {
        ObjectStat objectStat;
        try {
            objectStat = minioClient.statObject(configuration.getBucketName(), fileId.toString());
        } catch (ErrorResponseException e) {
            // not found
            return false;
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoResponseException | InvalidResponseException
                | XmlPullParserException | InvalidArgumentException | InsufficientDataException | InternalException
                | InvalidBucketNameException | IOException e) {
            throw new StorageException(e);
        }

        return objectStat != null;
    }
}
