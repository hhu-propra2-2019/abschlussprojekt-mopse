package mops.businesslogic;

import mops.persistence.file.FileInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FileService {
    /**
     * Returns all files of a group.
     *
     * @param account user credentials
     * @param groupId group identification
     * @return list of all files in that directory
     */
    List<FileInfo> getAllFilesOfGroup(Account account, long groupId);

    /**
     * Searches for files in a group.
     *
     * @param account user credentials
     * @param groupId group identification for the group to be searched
     * @param query   a query which specifies the serach
     * @return a list of files
     */
    List<FileInfo> searchFilesInGroup(Account account, long groupId, FileQuery query);

    /**
     * @param account user credentials
     * @param dirId   id of the folder
     * @return a list of file in that folder
     */
    List<FileInfo> getFilesOfDirectory(Account account, long dirId);

    /**
     * @param account user credentials
     * @param fileId  file id of needed file
     * @return file
     */
    FileInfo getFile(Account account, long fileId);

    /**
     * @param account user credentials
     * @param fileId  file id of file to be deleted
     * @return parent directory Id
     */
    long deleteFile(Account account, long fileId);
}
