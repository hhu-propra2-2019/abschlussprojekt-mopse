package mops.businesslogic.file;

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
     * Gets a directory id.
     *
     * @return parent directory of file
     */
    public long getDirectoryId() {
        return info.getDirectoryId();
    }

    /**
     * Gets the file id.
     *
     * @return file id
     */
    public long getId() {
        return info.getId();
    }

    /**
     * Gets the file type.
     *
     * @return content type of file
     */
    public String getType() {
        return info.getType();
    }

    /**
     * Gets the file name.
     *
     * @return display name of file
     */
    public String getName() {
        return info.getName();
    }

    /**
     * Gets the file size.
     *
     * @return size of file in bytes
     */
    public long getSize() {
        return info.getSize();
    }
}
