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
import mops.security.PermissionService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class DirectoryServiceImpl implements DirectoryService {

    /**
     * This connects to database related to directory information.
     */
    private final DirectoryRepository directoryRepository;

    /**
     * This connects to database related to File information.
     */
    private final FileInfoRepository fileInfoRepository;

    /**
     * API for GruppenFindung which handles permissions.
     */
    private final PermissionService permissionService;

    /**
     * This connects to database to handle directory permissions.
     */
    private final DirectoryPermissionsRepository directoryPermissionsRepository;

    /**
     * Uploads a file.
     *
     * @param account       user credentials
     * @param dirId         the id of the folder where the file will be uploaded
     * @param multipartFile the file object
     */
    @Override
    public FileInfo uploadFile(Account account, long dirId, MultipartFile multipartFile, Set<FileTag> fileTags) {

        FileInfo fileInfo = new FileInfo(multipartFile.getName(), dirId, multipartFile.getContentType(),
                multipartFile.getSize(), account.getName(), fileTags);

        fileInfoRepository.save(fileInfo);


        return fileInfo;
    }

    /**
     * Returns all folders of the parent folder.
     *
     * @param account     user credentials
     * @param parentDirID id of the parent folder
     * @return list of folders
     */
    @Override
    public List<Directory> getSubFolders(Account account, long parentDirID) {
        Directory directory = fetchDirectory(parentDirID);
        permissionService.fetchRoleForUserInGroup(account, directory);
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
    public Directory createRootFolder(Account account, Long groupId) {
        Directory directory = new Directory();
        directory.setName(groupId.toString());
        directory.setGroupOwner(groupId);
        permissionService.fetchRoleForUserInGroup(account, directory);
        Set<DirectoryPermissionEntry> permissions = defaultPermissions();
        DirectoryPermissions permission = new DirectoryPermissions(permissions);
        Long permissionId = directoryPermissionsRepository.save(permission).getId();
        directory.setPermissionsId(permissionId);
        return directoryRepository.save(directory);
    }

    /**
     * @return a set of the default permissions
     */
    private Set<DirectoryPermissionEntry> defaultPermissions() {
        return Set.of();
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
    public Directory createFolder(Account account, Long parentDirId, String dirName) {
        Directory rootDirectory = fetchDirectory(parentDirId);
        permissionService.fetchRoleForUserInGroup(account, rootDirectory);
        Directory directory = new Directory(dirName, rootDirectory.getId(), rootDirectory.getGroupOwner(), rootDirectory.getPermissionsId());
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
    public long deleteFolder(Account account, long dirId) {
        return 0;
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

    private Directory fetchDirectory(long parentDirID) {
        Optional<Directory> optionalDirectory = directoryRepository.findById(parentDirID);
        return optionalDirectory.orElseThrow(() -> new NoSuchElementException("There is no directory with the id: " + parentDirID + " in the database."));
    }
}
