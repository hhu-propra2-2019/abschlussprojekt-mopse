package mops.businesslogic;

import lombok.AllArgsConstructor;
import mops.exception.MopsException;
import mops.persistence.DirectoryPermissionsRepository;
import mops.persistence.directory.Directory;
import mops.persistence.permission.DirectoryPermissions;
import mops.security.PermissionService;
import mops.security.exception.DeleteAccessPermission;
import mops.security.exception.ReadAccessPermission;
import mops.security.exception.WriteAccessPermission;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Supplier;

@AllArgsConstructor
@Service
public class RoleServiceImpl {
    /**
     * API for GruppenFindung which handles permissions.
     */
    private final PermissionService permissionService;

    /**
     * This connects to database to handle directory permissions.
     */
    private final DirectoryPermissionsRepository directoryPermissionsRepo;

    /**
     * Checks if the user has writing rights.
     *
     * @param account   user credentials
     * @param directory id of the directory to check
     * @throws MopsException checked exception to present to UI
     */
    public void checkWritePermission(Account account, Directory directory) throws MopsException {
        DirectoryPermissions directoryPermissions = getDirectoryPermissions(directory);

        String userRole = permissionService.fetchRoleForUserInDirectory(account, directory);

        //this is not a violation of demeter's law
        boolean allowedToWrite = directoryPermissions.isAllowedToWrite(userRole); //NOPMD

        if (!allowedToWrite) {
            throw new WriteAccessPermission(String.format("The user %s doesn't have write access to %s.",
                    account.getName(),
                    directory.getName()));
        }
    }

    /**
     * Checks if the user has reading rights.
     *
     * @param account   user credentials
     * @param directory id of the directory to check
     * @throws MopsException checked exception to present to UI
     */
    public void checkReadPermission(Account account, Directory directory) throws MopsException {
        DirectoryPermissions directoryPermissions = getDirectoryPermissions(directory);

        String userRole = permissionService.fetchRoleForUserInDirectory(account, directory);
        //this is not a violation of demeter's law
        boolean allowedToRead = directoryPermissions.isAllowedToRead(userRole); //NOPMD

        if (!allowedToRead) {
            throw new ReadAccessPermission(String.format("The user %s doesn't have read access to %s.",
                    account.getName(),
                    directory.getName()));
        }

    }

    /**
     * Checks if the user has deleting rights.
     *
     * @param account   user credentials
     * @param directory id of the directory to check
     * @throws MopsException checked exception to present to UI
     */
    public void checkDeletePermission(Account account, Directory directory) throws MopsException {
        DirectoryPermissions directoryPermissions = getDirectoryPermissions(directory);

        String userRole = permissionService.fetchRoleForUserInDirectory(account, directory);

        //this is not a violation of demeter's law
        boolean allowedToDelete = directoryPermissions.isAllowedToDelete(userRole); //NOPMD

        if (!allowedToDelete) {
            throw new DeleteAccessPermission(String.format("The user %s doesn't have delete permission in %s.",
                    account.getName(),
                    directory.getName()));
        }
    }

    /**
     * @param account     user credentials
     * @param groupId     id of the group to check
     * @param allowedRole role which has the right
     * @throws MopsException checked exception to present to UI
     */
    public void checkIfRole(Account account, long groupId, String allowedRole) throws MopsException {
        String role = permissionService.fetchRoleForUserInGroup(account, groupId);
        if (!allowedRole.equals(role)) {
            String errorMessage = String.format(
                    "User is not %s of %d and there for not allowed to create a root folder.",
                    allowedRole,
                    groupId);
            throw new WriteAccessPermission(errorMessage);
        }
    }

    private DirectoryPermissions getDirectoryPermissions(Directory directory) throws MopsException {
        Optional<DirectoryPermissions> optDirPerm = directoryPermissionsRepo.findById(directory.getPermissionsId());
        //this is not a violation of demeter's law
        return optDirPerm.orElseThrow(getException(directory.getId())); //NOPMD
    }

    /**
     * @param dirId directory id
     * @return a supplier to throw a exception
     */
    private Supplier<MopsException> getException(long dirId) {
        return () -> { //NOPMD
            String errorMessage = String.format("There is no directory with the id: %d in the database.", dirId);
            return new DatabaseException(errorMessage);
        };
    }
}
