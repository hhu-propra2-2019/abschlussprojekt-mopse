package mops.businesslogic;

import lombok.AllArgsConstructor;
import mops.persistence.DirectoryPermissionsRepository;
import mops.persistence.DirectoryRepository;
import mops.persistence.FileInfoRepository;
import mops.persistence.directory.Directory;
import mops.persistence.file.FileInfo;
import mops.persistence.file.FileTag;
import mops.persistence.permission.DirectoryPermissionEntry;
import mops.persistence.permission.DirectoryPermissions;
import mops.security.DeleteAccessPermission;
import mops.security.PermissionService;
import mops.security.ReadAccessPermission;
import mops.security.exception.WriteAccessPermission;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DirectoryServiceImpl implements DirectoryService {

    /**
     * Represents the role of an admin.
     */
    public static final String ADMINISTRATOR = "administrator";
    /**
     * This connects to database related to directory information.
     */
    private final DirectoryRepository directoryRepository;

    /**
     * API for GruppenFindung which handles permissions.
     */
    private final PermissionService permissionService;

    /**
     * Handles meta data of files.
     */
    private final FileInfoService fileInfoService;

    /**
     * This connects to database to handle directory permissions.
     */
    private final DirectoryPermissionsRepository directoryPermissionsRepo;

    /**
     * Checks whether the user is authorized to load the file.
     *
     * @param account user credentials
     * @param dirId   the id of the folder where the file will be uploaded
     */
    @Override
    public void checkWritePermission(Account account, long dirId) throws WriteAccessPermission {
        Directory directory = fetchDirectory(dirId);

        checkWritePermission(account, directory);
    }

    /**
     * Returns all folders of the parent folder.
     *
     * @param account     user credentials
     * @param parentDirID id of the parent folder
     * @return list of folders
     */
    @Override
    public List<Directory> getSubFolders(Account account, long parentDirID) throws ReadAccessPermission {
        Directory directory = fetchDirectory(parentDirID);
        checkReadPermission(account, directory);
        return directoryRepository.getAllSubFoldersOfParent(parentDirID);
    }


    /**
     * Creates the group root directory.
     *
     * @param account user credentials
     * @param groupId the group id
     * @return the directory created
     */
    @Override
    public Directory createRootFolder(Account account, Long groupId) throws WriteAccessPermission {
        Directory directory = new Directory();
        directory.setName(groupId.toString());
        directory.setGroupOwner(groupId);
        checkIfAdmin(account, groupId);
        Set<String> roleNames = permissionService.fetchRolesInGroup(groupId);
        Set<DirectoryPermissionEntry> permissions = createDefaultPermissions(roleNames);
        DirectoryPermissions permission = new DirectoryPermissions(permissions);
        DirectoryPermissions rootPermissions = directoryPermissionsRepo.save(permission);
        directory.setPermission(rootPermissions);
        return directoryRepository.save(directory);
    }

    /**
     * Creates a new folder inside a folder.
     *
     * @param account     user credentials
     * @param parentDirId id of the parent folder
     * @param dirName     name of the new folder
     * @return id of the new folder
     */
    @Override
    public Directory createFolder(Account account, Long parentDirId, String dirName) throws WriteAccessPermission {
        Directory rootDirectory = fetchDirectory(parentDirId);
        checkWritePermission(account, rootDirectory);
        Directory directory = rootDirectory.createSubDirectory(dirName); //NOPMD// this is no violation of demeter's law
        return directoryRepository.save(directory);
    }

    /**
     * Deletes a folder.
     *
     * @param account user credential
     * @param dirId   id of the folder to be deleted
     * @return the parent id of the deleted folder
     */
    @Override
    public Directory deleteFolder(Account account, long dirId) throws DeleteAccessPermission, ReadAccessPermission {
        Directory directory = fetchDirectory(dirId);
        checkDeletePermission(account, directory);

        List<FileInfo> files = fileInfoService.fetchAllFilesInDirectory(dirId);
        List<Directory> subFolders = getSubFolders(account, dirId);

        if (!files.isEmpty() || !subFolders.isEmpty()) {
            throw new DeleteAccessPermission(String.format("The directory %s is not empty.", directory.getName()));
        }

        Directory parentDirectory = fetchDirectory(directory.getParentId());

        directoryRepository.delete(directory);

        return parentDirectory;
    }

    /**
     * Searches a folder for files.
     *
     * @param account user credentials
     * @param dirId   id of the folder to be searched
     * @param query   wrapper object of the query parameter
     * @return list of files
     */
    @Override
    public List<FileInfo> searchFolder(Account account, long dirId, FileQuery query) {
        return null;
    }

    /**
     * Replaces the permissions for a directory with new ones.
     *
     * @param account           user credentials
     * @param dirId             directory id of whom's permission should be changed
     * @param permissionEntries new set of permissions
     * @return the updated directory object
     */
    @Override
    public Directory updatePermission(Account account,
                                      Long dirId,
                                      Set<DirectoryPermissionEntry> permissionEntries) throws WriteAccessPermission {
        Directory directory = fetchDirectory(dirId);
        long groupId = directory.getGroupOwner();
        checkIfAdmin(account, groupId);
        DirectoryPermissions permissions = new DirectoryPermissions(directory.getPermissionsId(), permissionEntries);
        DirectoryPermissions savedPermissions = directoryPermissionsRepo.save(permissions);
        directory.setPermission(savedPermissions);
        return directoryRepository.save(directory);
    }

    /**
     * @param parentDirID the id of the parent folder
     * @return a directory object of the request folder
     */
    private Directory fetchDirectory(long parentDirID) {
        Optional<Directory> optionalDirectory = directoryRepository.findById(parentDirID);
        return optionalDirectory.orElseThrow(getException(parentDirID)); //NOPMD// this is not a violation of demeter's law
    }


    /**
     * Checks if the user has permission to write in that folder.
     *
     * @param account   user credentials
     * @param directory directory object of the permissions requested
     */
    private void checkWritePermission(Account account, Directory directory) throws WriteAccessPermission {
        DirectoryPermissions directoryPermissions = getDirectoryPermissions(directory);

        String userRole = permissionService.fetchRoleForUserInDirectory(account, directory);

        boolean allowedToWrite = directoryPermissions.getPermissions()
                .stream()
                .filter(DirectoryPermissionEntry::isCanWrite)
                .anyMatch(permission -> permission.getRole().equals(userRole));

        if (!allowedToWrite) {
            throw new WriteAccessPermission(String.format("The user %s doesn't have write access to %s.",
                    account.getName(),
                    directory.getName()));
        }
    }

    private void checkReadPermission(Account account, Directory directory) throws ReadAccessPermission {
        DirectoryPermissions directoryPermissions = getDirectoryPermissions(directory);

        String userRole = permissionService.fetchRoleForUserInDirectory(account, directory);

        boolean allowedToWrite = directoryPermissions.getPermissions()
                .stream()
                .filter(DirectoryPermissionEntry::isCanRead)
                .anyMatch(permission -> permission.getRole().equals(userRole));

        if (!allowedToWrite) {
            throw new ReadAccessPermission(String.format("The user %s doesn't have read access to %s.",
                    account.getName(),
                    directory.getName()));
        }

    }

    private void checkDeletePermission(Account account, Directory directory) throws DeleteAccessPermission {
        DirectoryPermissions directoryPermissions = getDirectoryPermissions(directory);

        String userRole = permissionService.fetchRoleForUserInDirectory(account, directory);

        boolean allowedToDelete = directoryPermissions.getPermissions()
                .stream()
                .filter(DirectoryPermissionEntry::isCanDelete)
                .anyMatch(permission -> permission.getRole().equals(userRole));

        if (!allowedToDelete) {
            throw new DeleteAccessPermission(String.format("The user %s doesn't have delete permission in %s.",
                    account.getName(),
                    directory.getName()));
        }
    }

    private void checkIfAdmin(Account account, Long groupId) throws WriteAccessPermission {
        String role = permissionService.fetchRoleForUserInGroup(account, groupId);
        if (!ADMINISTRATOR.equals(role)) {
            String errorMessage = String.format(
                    "User is not %s of %d and there for not allowed to create a root folder.",
                    ADMINISTRATOR,
                    groupId);
            throw new WriteAccessPermission(errorMessage);
        }
    }

    private DirectoryPermissions getDirectoryPermissions(Directory directory) {
        Optional<DirectoryPermissions> optDirPerm = directoryPermissionsRepo.findById(directory.getPermissionsId());
        return optDirPerm.orElseThrow(getException(directory.getId()));
    }


    /**
     * Creates the default permission set.
     *
     * @param roleNames all role names existing in the group
     * @return a set of directory permission entries
     */
    //TODO: this is a template and can only implement when GruppenFindung defined their roles.
    private Set<DirectoryPermissionEntry> createDefaultPermissions(Set<String> roleNames) {
        return roleNames.stream() //NOPMD// this is not a violation of demeter's law
                .map(role -> new DirectoryPermissionEntry(role, true, true, true))
                .collect(Collectors.toSet());
    }

    /**
     * @param dirId directory id
     * @return a supplier to throw a exception
     */
    private Supplier<NoSuchElementException> getException(long dirId) {
        return () -> { //NOPMD
            String errorMessage = String.format("There is no directory with the id: %d in the database.", dirId);
            return new NoSuchElementException(errorMessage);
        };
    }

}
