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

}
