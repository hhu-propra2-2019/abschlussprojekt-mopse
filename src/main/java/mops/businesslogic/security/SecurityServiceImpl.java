package mops.businesslogic.security;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mops.businesslogic.exception.DeleteAccessPermissionException;
import mops.businesslogic.exception.ReadAccessPermissionException;
import mops.businesslogic.exception.WriteAccessPermissionException;
import mops.businesslogic.group.GroupService;
import mops.businesslogic.permission.PermissionService;
import mops.exception.MopsException;
import mops.persistence.directory.Directory;
import mops.persistence.permission.DirectoryPermissions;
import org.springframework.stereotype.Service;

/**
 * Checks roles permissions.
 */
@Slf4j
@Service
@AllArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    /**
     * API for GruppenFindung which handles permissions.
     */
    private final GroupService groupService;
    /**
     * Handles directory permissions.
     */
    private final PermissionService permissionService;

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.LawOfDemeter")
    public void checkWritePermission(Account account, Directory directory) throws MopsException {
        DirectoryPermissions permissions = permissionService.getPermissions(directory);

        String userRole = groupService.fetchRoleForUserInGroup(account, directory.getGroupOwner());

        //this is not a violation of demeter's law
        boolean allowedToWrite = permissions.isAllowedToWrite(userRole);

        if (!allowedToWrite) {
            log.error("The user '{}' tried to write in '{}' where they have no write permissions.",
                    account.getName(),
                    directory.getName());
            throw new WriteAccessPermissionException(
                    String.format("Der Benutzer %s hat keine Schreibberechtigungen in %s.",
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
        DirectoryPermissions permissions = permissionService.getPermissions(directory);

        String userRole = groupService.fetchRoleForUserInGroup(account, directory.getGroupOwner());
        //this is not a violation of demeter's law
        boolean allowedToRead = permissions.isAllowedToRead(userRole);

        if (!allowedToRead) {
            log.error("The user '{}' tried to read in '{}' where they have no read permissions.",
                    account.getName(),
                    directory.getName());
            throw new ReadAccessPermissionException(
                    String.format("Der Benutzer %s hat keine Leseberechtigungen in %s.",
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
        DirectoryPermissions permissions = permissionService.getPermissions(directory);

        String userRole = groupService.fetchRoleForUserInGroup(account, directory.getGroupOwner());

        //this is not a violation of demeter's law
        boolean allowedToDelete = permissions.isAllowedToDelete(userRole);

        if (!allowedToDelete) {
            log.error("The user '{}' tried to delete in '{}' where they have no delete permissions.",
                    account.getName(),
                    directory.getName());
            throw new DeleteAccessPermissionException(
                    String.format("Der Benutzer %s hat keine Löschberechtigungen in %s.",
                            account.getName(),
                            directory.getName()));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkIfRole(Account account, long groupId, String allowedRole) throws MopsException {
        String role = groupService.fetchRoleForUserInGroup(account, groupId);
        if (!allowedRole.equals(role)) {
            log.error("The user '{}' does not have the required role '{}' in group with id {}.",
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
}
