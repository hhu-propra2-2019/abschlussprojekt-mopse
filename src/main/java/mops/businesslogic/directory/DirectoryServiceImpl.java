package mops.businesslogic.directory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mops.businesslogic.delete.DeleteService;
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
import mops.persistence.group.Group;
import mops.persistence.permission.DirectoryPermissions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;
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
    @Value("${material1.mops.configuration.role.admin}")
    private String adminRole = "admin";
    /**
     * The max amount of folders per group.
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    @Value("${material1.mops.configuration.quota.max-folders-in-group}")
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
     * Handle directory and file deletion;
     */
    private final DeleteService deleteService;
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
    @SuppressWarnings({ "PMD.AvoidCatchingGenericException", "PMD.LawOfDemeter" })
    public List<Directory> getSubFolders(Account account, long parentDirID) throws MopsException {
        Directory directory = getDirectory(parentDirID);
        securityService.checkReadPermission(account, directory);
        try {
            List<Directory> directories = new ArrayList<>(directoryRepository.getAllSubFoldersOfParent(parentDirID));
            directories.sort(Directory.NAME_COMPARATOR);
            if (directory.getParentId() == null) {
                // If the current dir is the root folder,
                // there could be directories in it without
                // reading permission
                directories = removeNoReadPermissionDirectories(account, directories);
            }
            return directories;
        } catch (Exception e) {
            log.error("Subfolders of parent folder with id '{}' could not be loaded:", parentDirID, e);
            throw new DatabaseException("Unterordner konnten nicht geladen werden.", e);
        }
    }

    /**
     * Removes all directories without reading permissions.
     *
     * @param account     the account
     * @param directories all directories that should be checked
     * @return filtered list
     * @throws MopsException on error
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    private List<Directory> removeNoReadPermissionDirectories(Account account,
                                                              List<Directory> directories) throws MopsException {
        List<Directory> readableFolders = new ArrayList<>();
        for (Directory dir : directories) {
            boolean readPerm = securityService.getPermissionsOfUser(account, dir).isRead();
            if (readPerm) {
                readableFolders.add(dir);
            }
        }
        return readableFolders;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "PMD.LawOfDemeter", "PMD.DataflowAnomalyAnalysis" })
    public List<Directory> getDirectoryPath(long dirId) throws MopsException {
        List<Directory> result = new LinkedList<>();
        Directory dir = getDirectory(dirId);
        while (dir.getParentId() != null) {
            result.add(dir);
            dir = getDirectory(dir.getParentId());
        }
        // add root
        result.add(dir);
        //reversing list
        Collections.reverse(result);
        return result;
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

        Group group = groupService.getGroup(groupId); // implicit check of existence

        DirectoryPermissions rootPermissions = groupService.getDefaultPermissions(groupId);
        rootPermissions = permissionService.savePermissions(rootPermissions);
        Directory directory = Directory.builder()
                .name(group.getName())
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

        return deleteService.deleteFolder(account, dirId);
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
    @SuppressWarnings("PMD.LawOfDemeter")
    public Directory editDirectory(
            Account account,
            long dirId,
            String newName,
            DirectoryPermissions newPermissions) throws MopsException {
        Directory directory = getDirectory(dirId);
        securityService.checkIfRole(account, directory.getGroupOwner(), adminRole);

        if (directory.getParentId() != null) {
            if (newName == null || newName.isEmpty()) {
                log.error("The user '{}' tried to change the name of a directory to an empty name.", account.getName());
                throw new DatabaseException("Name des Ordners darf nicht leer sein.");
            }
            directory.setName(newName);
        }

        updatePermission(account, dirId, newPermissions);

        return saveDirectory(directory);
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

        Set<String> roles = groupService.getRoles(directory.getGroupOwner());
        if (!permissions.getRoles().equals(roles)) {
            log.error("The user '{}' tried to change the permissions of a directory to an invalid one. "
                            + "Role Permissions are missing or superfluous.",
                    account.getName());
            throw new DatabaseException("Neue Berechtigungen ungültig.");
        }

        if (!permissions.isAllowedToRead(adminRole)
                || !permissions.isAllowedToWrite(adminRole)
                || !permissions.isAllowedToDelete(adminRole)) {
            log.error("The user '{}' tried to change the permissions of the admin role.", account.getName());
            throw new DatabaseException("Neue Berechtigungen ungültig.");
        }

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
            String error = String.format("Der Ordner '%s' konnte nicht gespeichert werden.", directory.getName());
            if (e.getCause() instanceof DuplicateKeyException) {
                error = String.format("Der Ordner '%s' existiert bereits.", directory.getName());
            }
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
}
