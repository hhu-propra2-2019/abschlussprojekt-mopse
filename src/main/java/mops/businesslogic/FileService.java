package mops.businesslogic;

import mops.exception.MopsException;
import mops.persistence.file.FileInfo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Service
public interface FileService {
    /**
     * Returns all files of a group.
     *
     * @param account user credentials
     * @param groupId group identification
     * @return list of all files in that directory
     */
    List<FileInfo> getAllFilesOfGroup(Account account, long groupId) throws MopsException;

    /**
     * @param account       user credentials
     * @param dirId         directory id of the future parent folder
     * @param multipartFile the binary code of the file
     * @param tags          the file tag
     */
    void uploadFile(Account account, long dirId, MultipartFile multipartFile, Set<String> tags) throws MopsException;

    /**
     * Searches for files in a group.
     *
     * @param account user credentials
     * @param groupId group identification for the group to be searched
     * @param query   a query which specifies the serach
     * @return a list of files
     */
    List<FileInfo> searchFilesInGroup(Account account, long groupId, FileQuery query) throws MopsException;

    /**
     * @param account user credentials
     * @param dirId   id of the folder
     * @return a list of file in that folder
     */
    List<FileInfo> getFilesOfDirectory(Account account, long dirId) throws MopsException;

    /**
     * @param account user credentials
     * @param fileId  file id of needed file
     * @return file
     */
    FileContainer getFile(Account account, long fileId) throws MopsException;

    /**
     * @param account user credentials
     * @param fileId  file id of needed file
     * @return file info
     */
    FileInfo getFileInfo(Account account, long fileId) throws MopsException;

    /**
     * @param account user credentials
     * @param fileId  file id of file to be deleted
     * @return parent directory Id
     */
    long deleteFile(Account account, long fileId) throws MopsException;
}
