package mops.businesslogic;

import mops.persistence.directory.Directory;
import mops.persistence.file.FileInfo;
import mops.persistence.file.FileTag;
import mops.security.ReadAccessPermission;
import mops.security.exception.WriteAccessPermission;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

public interface DirectoryService {
    /**
     * Uploads a file.
     *
     * @param account       user credentials
     * @param dirId         the id of the folder where the file will be uploaded
     * @param multipartFile the file object
     * @param fileTags      set of the file tags
     * @return the file meta object
     */
    FileInfo uploadFile(Account account, long dirId, MultipartFile multipartFile, Set<FileTag> fileTags);

    /**
     * Returns all folders of the parent folder.
     *
     * @param account user credentials
     * @param dirId   id of the folder
     * @return list of folders
     */
    List<Directory> getSubFolders(Account account, long dirId) throws ReadAccessPermission;

    /**
     * Creates the group root directory.
     *
     * @param account user credentials
     * @param groupId the group id
     * @return the directory created
     */
    Directory createRootFolder(Account account, Long groupId) throws WriteAccessPermission;

    /**
     * Creates a new folder inside a folder.
     *
     * @param account     user credentials
     * @param parentDirId id of the parent folder
     * @param dirName     name of the new folder
     * @return id of the new folder
     */
    Directory createFolder(Account account, Long parentDirId, String dirName) throws WriteAccessPermission;

    /**
     * Deletes a folder.
     *
     * @param account user credential
     * @param dirId   id of the folder to be deleted
     * @return the parent id of the deleted folder
     */
    long deleteFolder(Account account, long dirId);

    /**
     * Searches a folder for files.
     *
     * @param account user credentials
     * @param dirId   id of the folder to be searched
     * @param query   wrapper object of the query parameter
     * @return list of files
     */
    List<FileInfo> searchFolder(Account account, long dirId, FileQuery query);
}
