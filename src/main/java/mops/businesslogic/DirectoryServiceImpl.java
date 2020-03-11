package mops.businesslogic;

import lombok.AllArgsConstructor;
import mops.businesslogic.exception.DatabaseException;
import mops.businesslogic.exception.DeleteAccessPermission;
import mops.exception.MopsException;
import mops.persistence.DirectoryPermissionsRepository;
import mops.persistence.DirectoryRepository;
import mops.persistence.directory.Directory;
import mops.persistence.file.FileInfo;
import mops.persistence.permission.DirectoryPermissionEntry;
import mops.persistence.permission.DirectoryPermissions;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@SuppressWarnings("PMD.TooManyMethods") //this class needs more methods
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
     * Handle permission checks for roles.
     */
    private final RoleServiceImpl roleService;

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
    public void checkWritePermission(Account account, long dirId) throws MopsException {
        Directory directory = fetchDirectory(dirId);

        roleService.checkWritePermission(account, directory);
    }

    /**
     * Returns all folders of the parent folder.
     *
     * @param account     user credentials
     * @param parentDirID id of the parent folder
     * @return list of folders
     */
    @Override
    public List<Directory> getSubFolders(Account account, long parentDirID) throws MopsException {
        Directory directory = fetchDirectory(parentDirID);
        roleService.checkReadPermission(account, directory);
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
    public Directory createRootFolder(Account account, Long groupId) throws MopsException {

        roleService.checkIfRole(account, groupId, ADMINISTRATOR);
        Set<String> roleNames = permissionService.fetchRolesInGroup(groupId);
        Set<DirectoryPermissionEntry> permissions = createDefaultPermissions(roleNames);
        DirectoryPermissions permission = new DirectoryPermissions(permissions);
        DirectoryPermissions rootPermissions = directoryPermissionsRepo.save(permission);
        Directory directory = Directory.of(groupId.toString(), null, groupId, rootPermissions);
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
    public Directory createFolder(Account account, Long parentDirId, String dirName) throws MopsException {
        Directory rootDirectory = fetchDirectory(parentDirId);
        roleService.checkWritePermission(account, rootDirectory);
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
    @SuppressWarnings("PMD.LawOfDemeter") //these are not violations of demeter's law
    public Directory deleteFolder(Account account, long dirId) throws MopsException {
        Directory directory = fetchDirectory(dirId);
        roleService.checkDeletePermission(account, directory);

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
                                      long dirId,
                                      Set<DirectoryPermissionEntry> permissionEntries) throws MopsException {
        Directory directory = fetchDirectory(dirId);
        checkIfAdmin(account, directory);
        DirectoryPermissions directoryPermissions = fetchPermissions(directory);
        DirectoryPermissions updatedPermissions = DirectoryPermissions.of(directoryPermissions, permissionEntries);
        DirectoryPermissions savedPermissions = directoryPermissionsRepo.save(updatedPermissions);
        directory.setPermission(savedPermissions); //NOPMD demeter's law unable to fix
        return directoryRepository.save(directory);
    }

    /**
     * @param parentDirID the id of the parent folder
     * @return a directory object of the request folder
     */
    private Directory fetchDirectory(long parentDirID) {
        Optional<Directory> optionalDirectory = directoryRepository.findById(parentDirID);
        // this is not a violation of demeter's law
        return optionalDirectory.orElseThrow(getException(parentDirID)); //NOPMD//
    }

    private DirectoryPermissions fetchPermissions(Directory directory) throws DatabaseException {
        Optional<DirectoryPermissions> permissions = directoryPermissionsRepo.findById(directory.getPermissionsId());
        return permissions.orElseThrow(() -> { //NOPMD   // this is not a violation of demeter's law
            String errorMessage = "Permission couldn't be fetched.";
            return new DatabaseException(errorMessage);
        });
    }


    private void checkIfAdmin(Account account, Directory directory) throws MopsException {
        roleService.checkIfRole(account, directory.getId(), ADMINISTRATOR);
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
