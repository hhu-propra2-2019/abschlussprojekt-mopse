package mops.businesslogic;

import lombok.Value;
import mops.persistence.file.FileInfo;
import org.springframework.core.io.Resource;

/**
 * Wrapper of meta data and content of a file.
 */
@Value
// @Value automatically makes all fields `private final` which CheckStyle and PMD don't see
@SuppressWarnings({ "checkstyle:VisibilityModifier", "PMD.DefaultPackage" })
public class FileContainer {

    /**
     * contains info of file.
     */
    FileInfo info;
    /**
     * contains content stream.
     */
    Resource content;

    /**
     * @return parent directory of file
     */
    public long getDirectoryId() {
        return info.getDirectoryId();
    }

    /**
     * @return file id
     */
    public long getId() {
        return info.getId();
    }

    /**
     * @return content type of file
     */
    public String getType() {
        return info.getType();
    }

    /**
     * @return display name of file
     */
    public String getName() {
        return info.getName();
    }

    /**
     * @return size of file in bytes
     */
    public long getSize() {
        return info.getSize();
    }
}
