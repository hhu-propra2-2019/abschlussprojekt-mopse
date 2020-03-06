package mops.businesslogic;

import mops.persistence.Directory;
import mops.persistence.FileInfo;

import java.util.List;

public interface DirectoryService {
    /**
     * Uploads a file.
     *
     * @param account  user credentials
     * @param dirId    the id of the folder where the file will be uploaded
     * @param fileInfo the file object
     */
    void uploadFile(Account account, int dirId, FileInfo fileInfo);

    /**
     * Returns all folders of the parent folder.
     *
     * @param account user credentials
     * @param dirId   id of the folder
     * @return list of folders
     */
    List<Directory> getSubFolders(Account account, int dirId);
}
