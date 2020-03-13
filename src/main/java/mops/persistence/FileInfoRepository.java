package mops.persistence;

import mops.exception.MopsException;
import mops.persistence.file.FileInfo;
import mops.utils.AggregateBuilder;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AggregateBuilder
public interface FileInfoRepository extends CrudRepository<FileInfo, Long> {
    /**
     * @param dirId directory id
     * @return a list of files in that directory
     */
    default List<FileInfo> getAllFileInfoByDirectory(long dirId) {
        return List.of();
    }

    /**
     * @param fileId file id
     * @return a FileInfo object
     */
    default FileInfo getFileInfoById(long fileId) {
        return null;
    }

    /**
     * @param fileInfo Metadata of a file
     * @return ID the FileInfo was saved under
     */
    default FileInfo addFileInfoToDatabase(FileInfo fileInfo) {
        return null;
    }

    /**
     * @param fileId file id to be deleted
     */
    @SuppressWarnings("PMD")
    default void deleteFileInfoFromDatabase(long fileId) throws MopsException {
    }
}
