package mops.persistence;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mops.storage.minio")
@Getter
@Setter
public class StorageServiceConfiguration {


    /**
     * URL of the MinIO server.
     */
    private String url;
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
