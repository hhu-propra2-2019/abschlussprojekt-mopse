package mops.businesslogic.file;

import lombok.Value;
import mops.persistence.file.FileInfo;

/**
 * Wrapper for FileInfo and its permissions.
 */
@Value
// @Value automatically makes all fields `private final` which CheckStyle and PMD don't see
@SuppressWarnings({ "checkstyle:VisibilityModifier", "PMD.DefaultPackage" })
public class FileListEntry {

    /**
     * File.
     */
    FileInfo fileInfo;

}
