package mops.persistence;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mops.storage.minio")
@Getter
@Setter
public class FileRepositoryConfiguration {


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
