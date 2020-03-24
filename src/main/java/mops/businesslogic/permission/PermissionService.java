package mops.businesslogic.permission;

import mops.exception.MopsException;
import mops.persistence.directory.Directory;
import mops.persistence.permission.DirectoryPermissions;
import org.springframework.stereotype.Service;

/**
 * Handles directory permissions.
 */
@Service
public interface PermissionService {

    /**
     * Get permissions of directory.
     *
     * @param directory directory
     * @return directory permissions
     * @throws MopsException on error
     */
    DirectoryPermissions getPermissions(Directory directory) throws MopsException;

    /**
     * Save permissions of directory.
     *
     * @param permissions directory permissions
     * @return saved directory permissions
     * @throws MopsException on error
     */
    DirectoryPermissions savePermissions(DirectoryPermissions permissions) throws MopsException;

    /**
     * Deletes the permissions of a directory.
     *
     * @param directory directory of the permission to delete
     */
    void deletePermissions(Directory directory) throws MopsException;
}
