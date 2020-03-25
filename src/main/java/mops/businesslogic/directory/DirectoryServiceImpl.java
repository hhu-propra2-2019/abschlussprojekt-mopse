package mops.businesslogic.directory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mops.businesslogic.exception.DatabaseException;
import mops.businesslogic.exception.DeleteAccessPermissionException;
import mops.businesslogic.exception.StorageLimitationException;
import mops.businesslogic.file.FileInfoService;
import mops.businesslogic.file.query.FileQuery;
import mops.businesslogic.group.GroupRootDirWrapper;
import mops.businesslogic.group.GroupService;
import mops.businesslogic.permission.PermissionService;
import mops.businesslogic.security.Account;
import mops.businesslogic.security.SecurityService;
import mops.exception.MopsException;
import mops.persistence.DirectoryRepository;
import mops.persistence.directory.Directory;
import mops.persistence.directory.DirectoryBuilder;
import mops.persistence.file.FileInfo;
import mops.persistence.permission.DirectoryPermissions;
import mops.persistence.permission.DirectoryPermissionsBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handles meta data for directories.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DirectoryServiceImpl implements DirectoryService {

    /**
     * Represents the role of an admin.
     */
    @Value("${material1.mops.configuration.admin}")
    private String adminRole = "admin";
    /**
     * The max amount of folders per group.
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    @Value("${material1.mops.configuration.max-groups}")
    private long maxFoldersPerGroup = 200L;

    /**
     * This connects to database related to directory information.
     */
    private final DirectoryRepository directoryRepository;
    /**
     * Handles meta data of files.
     */
    private final FileInfoService fileInfoService;
    /**
     * Handle permission checks for roles.
     */
    private final SecurityService securityService;
    /**
     * Handle permissions storage and retrieval.
     */
    private final PermissionService permissionService;
    /**
     * Connects to the GruppenFindungs API.
     */
    private final GroupService groupService;

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public List<Directory> getSubFolders(Account account, long parentDirID) throws MopsException {
        Directory directory = getDirectory(parentDirID);
        securityService.checkReadPermission(account, directory);
        try {
            return directoryRepository.getAllSubFoldersOfParent(parentDirID);
        } catch (Exception e) {
            log.error("Subfolders of parent folder with id '{}' could not be loaded:", parentDirID, e);
            throw new DatabaseException("Unterordner konnten nicht geladen werden.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "PMD.LawOfDemeter", "PMD.OnlyOneReturn", "PMD.DataflowAnomalyAnalysis",
            "PMD.AvoidCatchingGenericException" })
    public GroupRootDirWrapper getOrCreateRootFolder(long groupId) throws MopsException {
        Optional<GroupRootDirWrapper> optRootDir;
        try {
            optRootDir = directoryRepository
                    .getRootFolder(groupId)
                    .map(GroupRootDirWrapper::new);
        } catch (Exception e) {
            log.error("Error while searching for root directory of group with id '{}':", groupId, e);
            throw new DatabaseException("Das Wurzelverzeichnis konnte nicht gefunden werden.", e);
        }

        if (optRootDir.isPresent()) {
            return optRootDir.get();
        }

        Set<String> roleNames = groupService.fetchRolesInGroup(groupId);
        if (roleNames.isEmpty()) { // TODO: check for actual existence of group
            log.error("A root directory for group '{}' could not be created, as the group does not exist.", groupId);
            String error = "Es konnte kein Wurzelverzeichnis für die Gruppe erstellt werden, da sie nicht existiert.";
            throw new MopsException(error);
        }
        DirectoryPermissions rootPermissions = createDefaultPermissions(roleNames);
        rootPermissions = permissionService.savePermissions(rootPermissions);
        Directory directory = Directory.builder()
                .name("")
                .groupOwner(groupId)
                .permissions(rootPermissions)
                .build(); // no demeter violation here
        return new GroupRootDirWrapper(saveDirectory(directory));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.LawOfDemeter")
    public Directory createFolder(Account account, long parentDirId, String dirName) throws MopsException {
        if (dirName.isEmpty()) {
            log.error("The user '{}' tried to create a sub folder with an empty name.", account.getName());
            throw new DatabaseException("Name leer.");
        }

        Directory parentDir = getDirectory(parentDirId);
        long groupFolderCount = getDirCountInGroup(parentDir.getGroupOwner());
        if (groupFolderCount >= maxFoldersPerGroup) {
            log.error("The user '{}' tried to create another sub folder in the group with the id {}, "
                            + "but they already reached their max allowed folder count.",
                    account.getName(),
                    parentDirId);
            String error = "Deine Gruppe hat die maximale Anzahl an Ordnern erreicht. "
                    + "Du kannst keine weiteren mehr erstellen.";
            throw new StorageLimitationException(error);
        }
        securityService.checkWritePermission(account, parentDir);

        DirectoryBuilder builder = Directory.builder() //this is no violation of demeter's law
                .fromParent(parentDir)
                .name(dirName);

        if (parentDir.getParentId() == null) {
            DirectoryPermissions parentPermissions = permissionService.getPermissions(parentDir);
            DirectoryPermissions permissions = DirectoryPermissions.builder()
                    .from(parentPermissions)
                    .id((Long) null)
                    .build();
            DirectoryPermissions savedPermissions = permissionService.savePermissions(permissions);
            builder.permissions(savedPermissions);
        }

        Directory directory = builder.build();
        return saveDirectory(directory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    @SuppressWarnings({ "PMD.LawOfDemeter", "PMD.DataflowAnomalyAnalysis" }) //these are not violations of demeter's law
    public Directory deleteFolder(Account account, long dirId) throws MopsException {
        Directory directory = getDirectory(dirId);
        securityService.checkDeletePermission(account, directory);

        List<FileInfo> files = fileInfoService.fetchAllFilesInDirectory(dirId);
        List<Directory> subFolders = getSubFolders(account, dirId);

        if (!files.isEmpty() || !subFolders.isEmpty()) {
            log.error("The user '{}' tried to delete the folder with id {}, but the folder was not empty.",
                    account.getName(),
                    dirId);
            String errorMessage = String.format("Der Ordner %s ist nicht leer.", directory.getName());
            throw new DeleteAccessPermissionException(errorMessage);
        }

        Directory parentDirectory = null;
        if (directory.getParentId() != null) {
            parentDirectory = getDirectory(directory.getParentId());
        }

        try {
            deleteDirectory(directory);

            if (parentDirectory == null || parentDirectory.getPermissionsId() != directory.getPermissionsId()) {
                permissionService.deletePermissions(directory);
            }
        } catch (MopsException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error("Error while deleting directory {} by user {}:", directory.getName(), account.getName(), e);
            throw new MopsException("Fehler während des Löschens aufgetreten", e);
        }

        return parentDirectory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.LawOfDemeter")
    public List<FileInfo> searchFolder(Account account, long dirId, FileQuery query) throws MopsException {
        Directory directory = getDirectory(dirId);
        securityService.checkReadPermission(account, directory);
        List<FileInfo> fileInfos = fileInfoService.fetchAllFilesInDirectory(dirId);

        List<FileInfo> results = fileInfos.stream() //this is a stream not violation of demeter's law
                .filter(query::checkMatch)
                .collect(Collectors.toList());

        for (Directory subDir : getSubFolders(account, dirId)) {
            results.addAll(searchFolder(account, subDir.getId(), query));
        }
        return results;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Directory editDirectory(
            Account account,
            long dirId,
            String newName,
            DirectoryPermissions newPermissions) throws MopsException {
        // TODO: this
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.LawOfDemeter")
    public DirectoryPermissions updatePermission(Account account,
                                                 long dirId,
                                                 DirectoryPermissions permissions) throws MopsException {
        Directory directory = getDirectory(dirId);
        securityService.checkIfRole(account, directory.getGroupOwner(), adminRole);
        DirectoryPermissions updatedPermissions = DirectoryPermissions.builder()
                .from(permissions)
                .id(directory.getPermissionsId())
                .build(); // no demeter violation here
        return permissionService.savePermissions(updatedPermissions);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "PMD.LawOfDemeter", "PMD.AvoidCatchingGenericException" })
    public Directory getDirectory(long dirId) throws MopsException {
        try {
            return directoryRepository.findById(dirId).orElseThrow();
        } catch (Exception e) {
            log.error("The directory with the id '{}' was requested, but was not found in the database:", dirId, e);
            String error = String.format("Der Ordner mit der ID '%d' konnte nicht gefunden werden.", dirId);
            throw new DatabaseException(error, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public Directory saveDirectory(Directory directory) throws MopsException {
        try {
            return directoryRepository.save(directory);
        } catch (Exception e) {
            log.error("The directory with the id '{}' could not be saved to the database:", directory, e);
            String error = String.format("Der Ordner '%s' konnte nicht gespeichert werden.", directory);
            throw new DatabaseException(error, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public void deleteDirectory(Directory directory) throws MopsException {
        try {
            directoryRepository.delete(directory);
        } catch (Exception e) {
            log.error("The directory '{}' could not be deleted from the database:", directory, e);
            String error = String.format("Der Ordner '%s' konnte nicht gelöscht werden.", directory);
            throw new DatabaseException(error, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public long getDirCountInGroup(long groupId) throws MopsException {
        try {
            return directoryRepository.getDirCountInGroup(groupId);
        } catch (Exception e) {
            log.error("Failed to get total directory count in group with id '{}':", groupId, e);
            throw new DatabaseException("Gesamtordneranzahl konnte nicht geladen werden!", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public long getTotalDirCount() throws MopsException {
        try {
            return directoryRepository.count();
        } catch (Exception e) {
            log.error("Failed to get total directory count:", e);
            throw new DatabaseException("Gesamtordneranzahl konnte nicht geladen werden!", e);
        }
    }

    /**
     * Creates the default permissions.
     *
     * @param roleNames all role names existing in the group
     * @return default directory permissions
     */
    //TODO: this is a placeholder and can only be implemented when GruppenFindung defined their roles.
    @SuppressWarnings({ "PMD.LawOfDemeter" }) //Streams
    private DirectoryPermissions createDefaultPermissions(Set<String> roleNames) {
        DirectoryPermissionsBuilder builder = DirectoryPermissions.builder();
        builder.entry(adminRole, true, true, true);
        roleNames
                .stream()
                .filter(role -> !role.equalsIgnoreCase(adminRole))
                .forEach(role -> builder.entry(role, true, false, false));
        return builder.build();
    }
}
