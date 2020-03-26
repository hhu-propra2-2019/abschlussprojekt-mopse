package mops.businesslogic.security;

import mops.exception.MopsException;
import mops.persistence.directory.Directory;
import org.springframework.stereotype.Service;

/**
 * Checks roles permissions.
 */
@Service
public interface SecurityService {

    /**
     * Gets all 3 permissions of a user in a directory.
     *
     * @param account   user credentials
     * @param directory the directory
     * @return a permission flag object
     */
    UserPermission getPermissionsOfUser(Account account, Directory directory) throws MopsException;

    /**
     * Checks if the user has writing rights.
     *
     * @param account   user credentials
     * @param directory id of the directory to check
     * @throws MopsException checked exception to present to UI
     */
    void checkWritePermission(Account account, Directory directory) throws MopsException;

    /**
     * Checks if the user has reading rights.
     *
     * @param account   user credentials
     * @param directory id of the directory to check
     * @throws MopsException checked exception to present to UI
     */
    void checkReadPermission(Account account, Directory directory) throws MopsException;

    /**
     * Checks if the user has deleting rights.
     *
     * @param account   user credentials
     * @param directory id of the directory to check
     * @throws MopsException checked exception to present to UI
     */
    void checkDeletePermission(Account account, Directory directory) throws MopsException;

    /**
     * Checks if user is has the admin role.
     *
     * @param account user credentials
     * @param groupId id of the group to check
     * @return true if admin
     */
    boolean isUserAdmin(Account account, long groupId) throws MopsException;

    /**
     * Checks if user is has a role.
     *
     * @param account     user credentials
     * @param groupId     id of the group to check
     * @param allowedRole role which has the right
     * @throws MopsException checked exception to present to UI
     */
    void checkIfRole(Account account, long groupId, String allowedRole) throws MopsException;

}
