package mops.businesslogic;

import mops.persistence.FileInfo;

public interface DirectoryService {
    /**
     * Uploads a file.
     *
     * @param account  user credentials
     * @param groupId  the id of the group where the file will be uploaded
     * @param fileInfo the file object
     */
    void uploadFile(Account account, int groupId, FileInfo fileInfo);
}
