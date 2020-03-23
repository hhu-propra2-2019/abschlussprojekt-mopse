package mops.persistence.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for MinIO.
 */
@Configuration
@ConfigurationProperties(prefix = "material1.mops.storage.minio")
@Getter
@Setter
public class FileRepositoryConfig {

    /**
     * Host of the MinIO server.
     */
    private String host;
    /**
     * Port of the MinIO server.
     */
    private int port;
    /**
     * Name of the bucket.
     */
    private String bucketName;
    /**
     * MinIO access key.
     */
    private String accessKey;
    /**
     * MinIO secret key.
     */
    private String secretKey;

}
