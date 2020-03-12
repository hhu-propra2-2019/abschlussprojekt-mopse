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
     * @param dirId directory id
     * @return a list of files in that directory
     */
    List<FileInfo> fetchAllFilesInDirectory(long dirId);

    /**
     *
     * @param fileId file id
     * @return a FileInfo object
     */
    FileInfo fetchFileInfo(long fileId);

    /**
     *
     * @param fileInfo Metadata of a file
     * @return ID the FileInfo was saved under
     */
    long saveFileInfo (FileInfo fileInfo);

    /**
     *
     * @param fileId file id
     */
    void deleteFileInfo (long fileId) throws MopsException;

    /**
     *
     * @param fileId file id
     * @return dir id
     */
    long fetchDirectoryId (long fileId);
}
