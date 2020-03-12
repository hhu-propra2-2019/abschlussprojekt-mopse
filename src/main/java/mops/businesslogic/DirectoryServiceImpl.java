package mops.businesslogic;

import lombok.AllArgsConstructor;
import mops.businesslogic.exception.DatabaseException;
import mops.businesslogic.exception.DeleteAccessPermissionException;
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
     * gets all 3 permissions of a user.
     *
     * @param account user credentials
     * @param dirId   the id of the folder
     * @return a permission flag object
     */
    @Override
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis") //this is normal behaviour
    public UserPermission getPermissionsOfUser(Account account, long dirId) {
        Directory directory = fetchDirectory(dirId);
        boolean write = true;
        boolean read = true;
        boolean delete = true;

        try {
            roleService.checkWritePermission(account, directory);
        } catch (MopsException e) {
            write = false;
        }

        try {
            roleService.checkReadPermission(account, directory);
        } catch (MopsException e) {
            read = false;
        }

        try {
            roleService.checkDeletePermission(account, directory);
        } catch (MopsException e) {
            delete = false;
        }

        return new UserPermission(read, write, delete);

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
    public Directory createRootFolder(Account account, long groupId) throws MopsException {
        roleService.checkIfRole(account, groupId, ADMINISTRATOR);
        Set<String> roleNames = roleService.fetchRolesInGroup(groupId);
        Set<DirectoryPermissionEntry> permissions = createDefaultPermissions(roleNames);
        DirectoryPermissions permission = new DirectoryPermissions(permissions);
        DirectoryPermissions rootPermissions = directoryPermissionsRepo.save(permission);
        Directory directory = Directory.of(String.valueOf(groupId), null, groupId, rootPermissions);
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
    @SuppressWarnings("PMD.LawOfDemeter")
    public Directory createFolder(Account account, long parentDirId, String dirName) throws MopsException {
        Directory rootDirectory = fetchDirectory(parentDirId);
        roleService.checkWritePermission(account, rootDirectory);
        Directory directory = rootDirectory.createSubDirectory(dirName); //this is no violation of demeter's law
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
            String errorMessage = String.format("The directory %s is not empty.", directory.getName());
            throw new DeleteAccessPermissionException(errorMessage);
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
    @SuppressWarnings("PMD.LawOfDemeter")
    public List<FileInfo> searchFolder(Account account, long dirId, FileQuery query) throws MopsException {
        Directory directory = fetchDirectory(dirId);
        roleService.checkReadPermission(account, directory);
        List<FileInfo> fileInfos = fileInfoService.fetchAllFilesInDirectory(dirId);

        return fileInfos.stream() //this is a stream not violation of demeter's law
                .filter(query::checkMatch)
                .collect(Collectors.toList());
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
    @SuppressWarnings("PMD.LawOfDemeter")
    public Directory updatePermission(Account account,
                                      long dirId,
                                      Set<DirectoryPermissionEntry> permissionEntries) throws MopsException {
        Directory directory = fetchDirectory(dirId);
        checkIfAdmin(account, directory);
        DirectoryPermissions directoryPermissions = fetchPermissions(directory);
        DirectoryPermissions updatedPermissions = DirectoryPermissions.of(directoryPermissions, permissionEntries);
        DirectoryPermissions savedPermissions = directoryPermissionsRepo.save(updatedPermissions);
        directory.setPermission(savedPermissions); //demeter's law unable to fix
        return directoryRepository.save(directory);
    }

    /**
     * @param parentDirID the id of the parent folder
     * @return a directory object of the request folder
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    private Directory fetchDirectory(long parentDirID) {
        Optional<Directory> optionalDirectory = directoryRepository.findById(parentDirID);
        // this is not a violation of demeter's law
        return optionalDirectory.orElseThrow(getException(parentDirID)); //this is not a violation of demeter's law
    }

    @SuppressWarnings("PMD.LawOfDemeter")
    private DirectoryPermissions fetchPermissions(Directory directory) throws DatabaseException {
        Optional<DirectoryPermissions> permissions = directoryPermissionsRepo.findById(directory.getPermissionsId());
        return permissions.orElseThrow(() -> { // this is not a violation of demeter's law
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
    @SuppressWarnings("PMD.LawOfDemeter")
    private Set<DirectoryPermissionEntry> createDefaultPermissions(Set<String> roleNames) {
        return roleNames.stream() //this is not a violation of demeter's law
                .map(role -> new DirectoryPermissionEntry(role, true, true, true))
                .collect(Collectors.toSet());
    }

    /**
     * @param dirId directory id
     * @return a supplier to throw a exception
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    private Supplier<NoSuchElementException> getException(long dirId) {
        return () -> { //this is not a violation of the demeter's law
            String errorMessage = String.format("There is no directory with the id: %d in the database.", dirId);
            return new NoSuchElementException(errorMessage);
        };
    }

}
