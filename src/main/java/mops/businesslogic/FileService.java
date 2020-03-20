package mops.businesslogic;

import mops.exception.MopsException;
import mops.persistence.directory.Directory;
import mops.persistence.file.FileInfo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;


/**
 * Handles communication with the storage service.
 */
@Service
public interface FileService {

    /**
     * Saves a file.
     * @param account       user credentials
     * @param dirId         directory id of the future parent folder
     * @param multipartFile the binary code of the file
     * @param tags          the file tag
     */
    void saveFile(Account account, long dirId, MultipartFile multipartFile, Set<String> tags) throws MopsException;

    /**
     * Gets a file.
     * @param account user credentials
     * @param fileId  file id of needed file
     * @return file
     */
    FileContainer getFile(Account account, long fileId) throws MopsException;

    /**
     * Deletes a file.
     * @param account user credentials
     * @param fileId  file id of file to be deleted
     * @return parent directory Id
     */
    Directory deleteFile(Account account, long fileId) throws MopsException;


    /**
     * Retrieves file meta data.
     *
     * @param account the account object.
     * @param fileId  the file ID
     * @return the meta info.
     * @throws MopsException on error.
     */
    FileInfo getFileInfo(Account account, long fileId) throws MopsException;

    /**
     * Retrieves file meta data of all files in a directory.
     *
     * @param account the account object.
     * @param dirId   the file ID
     * @return the meta info.
     * @throws MopsException on error.
     */
    List<FileInfo> getFilesOfDirectory(Account account, long dirId) throws MopsException;

    /**
     * Fetches all IDs.
     *
     * @return all File IDs
     */
    Set<Long> getAllFileIds() throws MopsException;

}
