package mops.businesslogic.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mops.businesslogic.exception.DeleteAccessPermissionException;
import mops.businesslogic.exception.ReadAccessPermissionException;
import mops.businesslogic.exception.WriteAccessPermissionException;
import mops.businesslogic.group.GroupService;
import mops.businesslogic.permission.PermissionService;
import mops.exception.MopsException;
import mops.persistence.directory.Directory;
import mops.persistence.group.Group;
import mops.persistence.permission.DirectoryPermissions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Checks roles permissions.
 */
@Slf4j
@Service
@RequiredArgsConstructor
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
     * Represents the role of an admin.
     */
    @Value("${material1.mops.configuration.role.admin}")
    @SuppressWarnings({ "PMD.ImmutableField", "PMD.BeanMembersShouldSerialize" })
    private String adminRole = "admin";

    /**
     * {@inheritDoc}
     */
    @Override
    //this is normal behaviour
    @SuppressWarnings({ "PMD.DataflowAnomalyAnalysis", "PMD.CyclomaticComplexity", "PMD.PrematureDeclaration" })
    public UserPermission getPermissionsOfUser(Account account, Directory directory) throws MopsException {
        boolean write = true;
        boolean read = true;
        boolean delete = true;

        try {
            checkWritePermission(account, directory);
        } catch (WriteAccessPermissionException e) {
            write = false;
        } catch (MopsException e) {
            throw new MopsException("Keine Berechtigungsprüfung auf Schreiben möglich", e);
        }

        try {
            checkReadPermission(account, directory);
        } catch (ReadAccessPermissionException e) {
            read = false;
        } catch (MopsException e) {
            throw new MopsException("Keine Berechtigungsprüfung auf Lesen möglich", e);
        }

        try {
            checkDeletePermission(account, directory);
        } catch (DeleteAccessPermissionException e) {
            delete = false;
        } catch (MopsException e) {
            throw new MopsException("Keine Berechtigungsprüfung auf Löschen möglich", e);
        }

        return new UserPermission(read, write, delete);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.LawOfDemeter")
    public void checkWritePermission(Account account, Directory directory) throws MopsException {
        DirectoryPermissions permissions = permissionService.getPermissions(directory);
        Group group = groupService.getGroup(directory.getGroupOwner());

        String userRole = group.getMemberRole(account.getName());

        //this is not a violation of demeter's law
        boolean allowedToWrite = permissions.isAllowedToWrite(userRole);

        if (!allowedToWrite) {
            log.debug("The user '{}' has no write permissions in '{}' .",
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
        Group group = groupService.getGroup(directory.getGroupOwner());

        String userRole = group.getMemberRole(account.getName());

        //this is not a violation of demeter's law
        boolean allowedToRead = permissions.isAllowedToRead(userRole);

        if (!allowedToRead) {
            log.debug("The user '{}' has no read permissions in '{}' .",
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
        Group group = groupService.getGroup(directory.getGroupOwner());

        String userRole = group.getMemberRole(account.getName());

        //this is not a violation of demeter's law
        boolean allowedToDelete = permissions.isAllowedToDelete(userRole);

        if (!allowedToDelete) {
            log.debug("The user '{}' has no delete permissions in '{}' .",
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
    @SuppressWarnings("PMD.OnlyOneReturn")
    public boolean isUserAdmin(Account account, long groupId) throws MopsException {
        try {
            checkIfRole(account, groupId, adminRole);
            return true;
        } catch (WriteAccessPermissionException e) {
            return false;
        } catch (MopsException e) {
            throw new MopsException("Keine Rollenprüfung auf admin möglich.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.LawOfDemeter")
    public void checkIfRole(Account account, long groupId, String allowedRole) throws MopsException {
        Group group = groupService.getGroup(groupId);

        String userRole = group.getMemberRole(account.getName());

        if (!allowedRole.equals(userRole)) {
            log.error("The user '{}' does not have the required role '{}' in group with id {}.",
                    account.getName(),
                    allowedRole,
                    groupId);
            String errorMessage = String.format(
                    "User is not %s of %s and therefore not allowed to do this action.",
                    allowedRole,
                    groupId);
            throw new WriteAccessPermissionException(errorMessage);
        }
    }
}
