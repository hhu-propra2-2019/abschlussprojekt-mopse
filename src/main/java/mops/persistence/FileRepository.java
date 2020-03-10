package mops.persistence;

import io.minio.MinioClient;
import io.minio.ObjectStat;
import io.minio.errors.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashMap;


@Repository
public class FileRepository {

    /**
     * The MinIO client.
     */
    private MinioClient minioClient;

    /**
     * Injected MinIO configuration.
     */
    private FileRepositoryConfig configuration;

    /**
     * Connects to MinIO Server and checks if the bucket exists.
     * @param configuration the injected Conf.
     */
    public FileRepository(FileRepositoryConfig configuration) {
        this.configuration = configuration;
        try {
            this.minioClient = new MinioClient(
                    configuration.getHost(),
                    configuration.getPort(),
                    configuration.getAccessKey(),
                    configuration.getSecretKey()
            );

            if (!minioClient.bucketExists(configuration.getBucketName())) {
                minioClient.makeBucket(configuration.getBucketName());
            }

            System.err.println("SUCCESS");
        } catch (InvalidEndpointException e) {
            System.err.println("MinIO endpoint not found: "
                    + configuration.getHost()
                    + ":"
                    + configuration.getPort());
            System.exit(1);
        } catch (InvalidPortException e) {
            System.err.println("MinIO port invalid.");
            System.exit(1);
        } catch (Exception e) {
            // Multiple, unlikely exceptions ¯\_(ツ)_/¯
            System.err.println(e.getClass().toString() + ": " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Saves a file.
     * @param file the file that should get saved permanently.
     * @param fileId the File ID given by the FileInfo database.
     * @return true if successful; false if not.
     */
    public boolean saveFile(MultipartFile file, Long fileId) {

        try {
            minioClient.putObject(configuration.getBucketName(),
                    fileId.toString(),
                    file.getInputStream(),
                    file.getSize(),
                    new HashMap<>(),
                    null, // no encryption will be needed
                    file.getContentType()
            );
        } catch (Exception e) {
            System.err.println("WTF!?: " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Deletes a file permanently.
     * @param fileId the ID of the file that's desired to be deleted.
     * @return true if successful; false if not.
     */
    public boolean deleteFile(Long fileId) {
        try {
            minioClient.removeObject(
                    configuration.getBucketName(),
                    fileId.toString()
            );
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return false;
        }

        return !isFileAvailable(fileId);
    }
    /**
     * Retrieves the bytes of the file.
     * @param fileId the ID of the file that's desired to be returned.
     * @return file content as InputStream.
     */
    public byte[] getFileContent(Long fileId) {
        byte[] content;
        try {
            content =  minioClient.getObject(
                    configuration.getBucketName(),
                    fileId.toString()
            ).readAllBytes();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
        return content;
    }

    /**
     * Checks if a file with a specified ID already exists.
     * @param fileId the file ID
     * @return true if found
     */
    public boolean isFileAvailable(Long fileId) {
        ObjectStat objectStat;
        try {
            objectStat = minioClient.statObject(configuration.getBucketName(), fileId.toString());
        } catch (ErrorResponseException e) {
            // not found
            return false;
        } catch (Exception e) {
            System.err.println(e.getClass()
                    + ": "
                    + e.getMessage()
            );
            return false;
        }

        if (objectStat == null) {
            return false;
        }

        return objectStat.length() > 0;
    }
}
