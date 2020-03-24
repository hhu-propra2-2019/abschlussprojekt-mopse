package mops.businesslogic.permission;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mops.businesslogic.exception.DatabaseException;
import mops.exception.MopsException;
import mops.persistence.DirectoryPermissionsRepository;
import mops.persistence.directory.Directory;
import mops.persistence.permission.DirectoryPermissions;
import org.springframework.stereotype.Service;

/**
 * Implementation for the permissions service.
 */
@Slf4j
@Service
@AllArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    /**
     * Permissions repository.
     */
    private final DirectoryPermissionsRepository permissionsRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "PMD.LawOfDemeter", "PMD.AvoidCatchingGenericException" })
    public DirectoryPermissions getPermissions(Directory directory) throws MopsException {
        long id = directory.getPermissionsId();
        try {
            return permissionsRepository.findById(id).orElseThrow();
        } catch (Exception e) {
            log.error("Failed to retrieve directory permissions with id '{}' for directory with id '{}' and name '{}':",
                    id, directory.getId(), directory.getName(), e);
            throw new DatabaseException("Die Ordnerberechtigungen konnten nicht gefunden werden!", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public DirectoryPermissions savePermissions(DirectoryPermissions permissions) throws MopsException {
        try {
            return permissionsRepository.save(permissions);
        } catch (Exception e) {
            log.error("Failed to save directory permissions '{}' to database:", permissions, e);
            throw new DatabaseException("Die Ordnerberechtigungen konnten nicht gespeichert werden!", e);
        }
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void delete(Directory directory) throws MopsException {
        try {
            permissionsRepository.deleteById(directory.getPermissionsId());
        } catch (Exception e) {
            log.error("Failed to delete directory permissions '{}'.", directory.getPermissionsId(), e);
            String message = String.format("Die Berechtigungnen für den Ordner '%s' konnten nicht gelöscht werden.",
                    directory.getName());
            throw new DatabaseException(message,
                    e);
        }
    }
}
