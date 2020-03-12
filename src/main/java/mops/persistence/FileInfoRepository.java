package mops.persistence;

import mops.exception.MopsException;
import mops.persistence.file.FileInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileInfoRepository extends CrudRepository<FileInfo, Long> {
    /**
     * @param dirId directory id
     * @return a list of files in that directory
     */
    List<FileInfo> getAllFileInfoByDirectory(long dirId);

    /**
     *
     * @param fileId file id
     * @return a FileInfo object
     */
    FileInfo getFileInfoById(long fileId);

    /**
     *
     * @param fileInfo Metadata of a file
     * @return ID the FileInfo was saved under
     */
    long addFileInfoToDatabase (FileInfo fileInfo);

    /**
     *
     * @param fileId file id to be deleted
     */
    void deleteFileInfoFromDatabase (long fileId) throws MopsException;

    /**
     *
     * @param fileId file id
     * @return directory id from file id
     */
    long getDirectoryIdByFileId (long fileId);
}
