package mops.businesslogic;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@AllArgsConstructor
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
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.LawOfDemeter")
    public void checkWritePermission(Account account, Directory directory) throws MopsException {
        DirectoryPermissions directoryPermissions = getDirectoryPermissions(directory);

        String userRole = permissionService.fetchRoleForUserInGroup(account, directory.getGroupOwner());

        //this is not a violation of demeter's law
        boolean allowedToWrite = directoryPermissions.isAllowedToWrite(userRole);

        if (!allowedToWrite) {
            log.error("The user '%s' tried to write to '%s' where has no writing permissions.",
                    account.getName(),
                    directory.getName());
            throw new WriteAccessPermissionException(String.format("The user %s doesn't have write access to %s.",
                    account.getName(),
                    directory.getName()));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.LawOfDemeter")
    public void checkReadPermission(Account account, Directory directory) throws MopsException {
        DirectoryPermissions directoryPermissions = getDirectoryPermissions(directory);

        String userRole = permissionService.fetchRoleForUserInGroup(account, directory.getGroupOwner());
        //this is not a violation of demeter's law
        boolean allowedToRead = directoryPermissions.isAllowedToRead(userRole);

        if (!allowedToRead) {
            log.error("The user '%s' tried to read to '%s' where has no reading permissions.",
                    account.getName(),
                    directory.getName());
            throw new ReadAccessPermissionException(String.format("The user %s doesn't have read access to %s.",
                    account.getName(),
                    directory.getName()));
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.LawOfDemeter")
    public void checkDeletePermission(Account account, Directory directory) throws MopsException {
        DirectoryPermissions directoryPermissions = getDirectoryPermissions(directory);

        String userRole = permissionService.fetchRoleForUserInGroup(account, directory.getGroupOwner());

        //this is not a violation of demeter's law
        boolean allowedToDelete = directoryPermissions.isAllowedToDelete(userRole);

        if (!allowedToDelete) {
            log.error("The user '%s' tried to delete to '%s' where has no deleting permissions.",
                    account.getName(),
                    directory.getName());
            throw new DeleteAccessPermissionException(String.format("The user %s doesn't have delete permission in %s.",
                    account.getName(),
                    directory.getName()));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkIfRole(Account account, long groupId, String allowedRole) throws MopsException {
        String role = permissionService.fetchRoleForUserInGroup(account, groupId);
        if (!allowedRole.equals(role)) {
            log.error("The user '%s' has not the required role '%s' in group with id %d.",
                    account.getName(),
                    allowedRole,
                    groupId);
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
            log.error("There is no directory with the id: %d in the database.", dirId);
            String errorMessage = String.format("There is no directory with the id: %d in the database.", dirId);
            return new DatabaseException(errorMessage);
        };
    }
}
