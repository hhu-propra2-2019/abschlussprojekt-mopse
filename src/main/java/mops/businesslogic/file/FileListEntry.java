package mops.businesslogic.file;

import lombok.Value;
import mops.businesslogic.security.UserPermission;
import mops.persistence.file.FileInfo;

import java.time.Instant;

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
    /**
     * User Permissions in containing folder.
     */
    UserPermission userPermission;
    /**
     * Is the user an admin of the group.
     */
    boolean isAdmin;
    /**
     * Is the user the owner of the file.
     */
    boolean isOwner;
    /**
     * Current time.
     */
    Instant now;

    /**
     * Checks whether this file list entry can be accessed because the user is privileged (admin or owner).
     *
     * @return true if privileged
     */
    public boolean isPrivileged() {
        return isAdmin || isOwner;
    }

    /**
     * Checks whether this file list entry can be read.
     *
     * @return true if read is allowed
     */
    public boolean isRead() {
        boolean read = userPermission.isRead() && isAvailable(now);
        return isPrivileged() || read;
    }

    /**
     * Checks whether this file list entry can be edited.
     *
     * @return true if edit is allowed
     */
    public boolean isEdit() {
        boolean edit = userPermission.isWrite() && userPermission.isDelete();
        return isPrivileged() || edit;
    }

    /**
     * Checks whether this file list entry can be deleted.
     *
     * @return true if delete is allowed
     */
    public boolean isDelete() {
        boolean delete = userPermission.isDelete();
        return isPrivileged() || delete;
    }

    /**
     * File id.
     *
     * @return file id
     */
    public Long getId() {
        return fileInfo.getId();
    }

    /**
     * File type.
     *
     * @return file type
     */
    public String getType() {
        return fileInfo.getType();
    }

    /**
     * File name.
     *
     * @return file name
     */
    public String getName() {
        return fileInfo.getName();
    }

    /**
     * Id of the Directory this file resides in.
     *
     * @return directory id
     */
    public long getDirectoryId() {
        return fileInfo.getDirectoryId();
    }

    /**
     * File size.
     *
     * @return file size
     */
    public long getSize() {
        return fileInfo.getSize();
    }

    /**
     * File size string.
     *
     * @return file size string
     */
    public String getSizeString() {
        return fileInfo.getSizeString();
    }

    /**
     * File owner.
     *
     * @return file owner
     */
    public String getOwner() {
        return fileInfo.getOwner();
    }

    /**
     * File available from time.
     *
     * @return file available from time
     */
    public Instant getAvailableFrom() {
        return fileInfo.getAvailableFrom();
    }

    /**
     * File available to time.
     *
     * @return file available to time
     */
    public Instant getAvailableTo() {
        return fileInfo.getAvailableTo();
    }

    /**
     * Tests whether this file is available at the given time.
     *
     * @param time current time
     * @return if this file is available
     */
    public boolean isAvailable(Instant time) {
        return fileInfo.isAvailable(time);
    }

    /**
     * File creation time.
     *
     * @return file creation time
     */
    public Instant getCreationTime() {
        return fileInfo.getCreationTime();
    }

    /**
     * File last modified time.
     *
     * @return file last modified time
     */
    public Instant getLastModifiedTime() {
        return fileInfo.getLastModifiedTime();
    }
}
