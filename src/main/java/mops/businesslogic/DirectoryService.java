package mops.businesslogic;

import mops.exception.MopsException;
import mops.persistence.directory.Directory;
import mops.persistence.file.FileInfo;
import mops.persistence.permission.DirectoryPermissions;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DirectoryService {

    /**
     * gets all 3 permissions of a user.
     *
     * @param account user credentials
     * @param dirId   the id of the folder
     * @return a permission flag object
     */
    UserPermission getPermissionsOfUser(Account account, long dirId);

    /**
     * Returns all folders of the parent folder.
     *
     * @param account user credentials
     * @param dirId   id of the folder
     * @return list of folders
     */
    List<Directory> getSubFolders(Account account, long dirId) throws MopsException;

    /**
     * Creates the group root directory.
     *
     * @param account user credentials
     * @param groupId the group id
     * @return the directory created
     */
    Directory createRootFolder(Account account, long groupId) throws MopsException;

    /**
     * Creates a new folder inside a folder.
     *
     * @param account     user credentials
     * @param parentDirId id of the parent folder
     * @param dirName     name of the new folder
     * @return id of the new folder
     */
    Directory createFolder(Account account, long parentDirId, String dirName) throws MopsException;

    /**
     * Deletes a folder.
     *
     * @param account user credential
     * @param dirId   id of the folder to be deleted
     * @return parent directory of the deleted folder
     */
    Directory deleteFolder(Account account, long dirId) throws MopsException;

    /**
     * Searches a folder for files.
     *
     * @param account user credentials
     * @param dirId   id of the folder to be searched
     * @param query   wrapper object of the query parameter
     * @return list of files
     */
    List<FileInfo> searchFolder(Account account, long dirId, FileQuery query) throws MopsException;

    /**
     * Replaces the permissions for a directory with new ones.
     *
     * @param account     user credentials
     * @param dirId       directory id of whom's permission should be changed
     * @param permissions new permissions
     * @return the updated directory
     */
    Directory updatePermission(Account account,
                               long dirId,
                               DirectoryPermissions permissions) throws MopsException;
}
