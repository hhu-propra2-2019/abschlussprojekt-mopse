package mops.businesslogic;

import mops.businesslogic.query.FileQuery;
import mops.exception.MopsException;
import mops.persistence.directory.Directory;
import mops.persistence.file.FileInfo;
import mops.persistence.permission.DirectoryPermissions;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DirectoryService {

    /**
     * Gets all 3 permissions of a user.
     *
     * @param account user credentials
     * @param dirId   the id of the folder
     * @return a permission flag object
     */
    UserPermission getPermissionsOfUser(Account account, long dirId) throws MopsException;

    /**
     * Returns all folders of the parent folder.
     *
     * @param account     user credentials
     * @param parentDirID id of the parent folder
     * @return list of folders
     */
    List<Directory> getSubFolders(Account account, long parentDirID) throws MopsException;

    /**
     * Creates the group root directory.
     *
     * @param groupId the group id
     * @return the directory created
     */
    Directory getOrCreateRootFolder(long groupId) throws MopsException;

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
     * @return the updated directory permissions
     */
    DirectoryPermissions updatePermission(Account account,
                                          long dirId,
                                          DirectoryPermissions permissions) throws MopsException;

    /**
     * Internal use only: possible security flaw!
     * Check permission before fetching!
     *
     * @param dirId the id of the parent folder
     * @return directory object of the requested folder
     * @throws MopsException on error
     */
    Directory getDirectory(long dirId) throws MopsException;

    /**
     * Get the total number of directories in a group.
     *
     * @param groupId group
     * @return directory count
     */
    long getDirCountInGroup(long groupId) throws MopsException;

    /**
     * Get the total number of directories in all groups.
     *
     * @return directory count
     */
    long getTotalDirCount() throws MopsException;

}
