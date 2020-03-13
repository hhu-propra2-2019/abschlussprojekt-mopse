package mops.persistence;

import mops.persistence.file.FileInfo;
import mops.utils.AggregateBuilder;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AggregateBuilder
public interface FileInfoRepository extends CrudRepository<FileInfo, Long> {

    /**
     * @param dirId directory id
     * @return a list of files in that directory
     */
    @Query("SELECT * FROM file_info WHERE directory_id = :dirId")
    List<FileInfo> findAllInDirectory(@Param("dirId") long dirId);

}
