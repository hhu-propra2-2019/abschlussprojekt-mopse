package mops.businesslogic;

import lombok.AllArgsConstructor;
import mops.businesslogic.exception.DatabaseException;
import mops.businesslogic.exception.DeleteAccessPermissionException;
import mops.businesslogic.exception.ReadAccessPermissionException;
import mops.businesslogic.exception.WriteAccessPermissionException;
import mops.exception.MopsException;
import mops.persistence.DirectoryPermissionsRepository;
import mops.persistence.directory.Directory;
import mops.persistence.permission.DirectoryPermissions;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Supplier;

@AllArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {

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
    @Override
    @SuppressWarnings("PMD.LawOfDemeter")
    public void checkWritePermission(Account account, Directory directory) throws MopsException {
        DirectoryPermissions directoryPermissions = getDirectoryPermissions(directory);

        String userRole = permissionService.fetchRoleForUserInGroup(account, directory.getGroupOwner());

        //this is not a violation of demeter's law
        boolean allowedToWrite = directoryPermissions.isAllowedToWrite(userRole);

        if (!allowedToWrite) {
            throw new WriteAccessPermissionException(String.format("The user %s doesn't have write access to %s.",
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
    @Override
    @SuppressWarnings("PMD.LawOfDemeter")
    public void checkReadPermission(Account account, Directory directory) throws MopsException {
        DirectoryPermissions directoryPermissions = getDirectoryPermissions(directory);

        String userRole = permissionService.fetchRoleForUserInGroup(account, directory.getGroupOwner());
        //this is not a violation of demeter's law
        boolean allowedToRead = directoryPermissions.isAllowedToRead(userRole);

        if (!allowedToRead) {
            throw new ReadAccessPermissionException(String.format("The user %s doesn't have read access to %s.",
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
    @Override
    @SuppressWarnings("PMD.LawOfDemeter")
    public void checkDeletePermission(Account account, Directory directory) throws MopsException {
        DirectoryPermissions directoryPermissions = getDirectoryPermissions(directory);

        String userRole = permissionService.fetchRoleForUserInGroup(account, directory.getGroupOwner());

        //this is not a violation of demeter's law
        boolean allowedToDelete = directoryPermissions.isAllowedToDelete(userRole);

        if (!allowedToDelete) {
            throw new DeleteAccessPermissionException(String.format("The user %s doesn't have delete permission in %s.",
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
    @Override
    public void checkIfRole(Account account, long groupId, String allowedRole) throws MopsException {
        String role = permissionService.fetchRoleForUserInGroup(account, groupId);
        if (!allowedRole.equals(role)) {
            String errorMessage = String.format(
                    "User is not %s of %d and therefore not allowed to do this action.",
                    allowedRole,
                    groupId);
            throw new WriteAccessPermissionException(errorMessage);
        }
    }

    @SuppressWarnings("PMD.LawOfDemeter")
    private DirectoryPermissions getDirectoryPermissions(Directory directory) throws MopsException {
        Optional<DirectoryPermissions> optDirPerm = directoryPermissionsRepo.findById(directory.getPermissionsId());
        //this is not a violation of demeter's law
        return optDirPerm.orElseThrow(getException(directory.getId()));
    }

    /**
     * @param dirId directory id
     * @return a supplier to throw a exception
     */
    private Supplier<MopsException> getException(long dirId) {
        return () -> {
            String errorMessage = String.format("There is no directory with the id: %d in the database.", dirId);
            return new DatabaseException(errorMessage);
        };
    }
}