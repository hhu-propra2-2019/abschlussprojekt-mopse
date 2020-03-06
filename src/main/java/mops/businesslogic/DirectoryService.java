package mops.businesslogic;

import mops.persistence.FileInfo;

public interface DirectoryService {
    /**
     * Uploads a file.
     *
     * @param account  user credentials
     * @param dirId    the id of the folder where the file will be uploaded
     * @param fileInfo the file object
     */
    void uploadFile(Account account, int dirId, FileInfo fileInfo);
}
