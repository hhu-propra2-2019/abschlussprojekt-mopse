package mops.businesslogic;

import mops.exception.MopsException;
import mops.persistence.file.FileInfo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Handles meta data for files.
 */
@Service
public interface FileInfoService {

    /**
     * Lists all files in a specific directory.
     *
     * @param dirId directory id
     * @return a list of files in that directory
     */
    List<FileInfo> fetchAllFilesInDirectory(long dirId) throws MopsException;

    /**
     * Get a file by id.
     *
     * @param fileId file id
     * @return a FileInfo object
     */
    FileInfo fetchFileInfo(long fileId) throws MopsException;

    /**
     * Save file to database.
     *
     * @param fileInfo Metadata of a file
     * @return the freshly saved FileInfo
     */
    FileInfo saveFileInfo(FileInfo fileInfo) throws MopsException;

    /**
     * Delete file from database by id.
     *
     * @param fileId file id
     */
    void deleteFileInfo(long fileId) throws MopsException;

}