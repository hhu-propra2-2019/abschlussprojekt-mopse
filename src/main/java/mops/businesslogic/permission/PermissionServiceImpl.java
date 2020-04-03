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
    @SuppressWarnings("PMD.LawOfDemeter")
    public DirectoryPermissions getPermissions(Directory directory) throws MopsException {
        long id = directory.getPermissionsId();
        try {
            return permissionsRepository.findById(id).orElseThrow();
        } catch (RuntimeException e) {
            log.error("Failed to retrieve directory permissions with id '{}' for directory with id '{}' and name '{}':",
                    id, directory.getId(), directory.getName(), e);
            String message = String.format("Die Berechtigungen für den Ordner '%s' konnten nicht gefunden werden.",
                    directory.getName());
            throw new DatabaseException(message, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectoryPermissions savePermissions(DirectoryPermissions permissions) throws MopsException {
        try {
            return permissionsRepository.save(permissions);
        } catch (RuntimeException e) {
            log.error("Failed to save directory permissions '{}' to database:", permissions, e);
            throw new DatabaseException("Die Ordnerberechtigungen konnten nicht gespeichert werden!", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deletePermissions(Directory directory) throws MopsException {
        long id = directory.getPermissionsId();
        try {
            permissionsRepository.deleteById(id);
        } catch (RuntimeException e) {
            log.error("Failed to delete directory permissions with id '{}':", id, e);
            String message = String.format("Die Berechtigungen für den Ordner '%s' konnten nicht gelöscht werden.",
                    directory.getName());
            throw new DatabaseException(message, e);
        }
    }
}
