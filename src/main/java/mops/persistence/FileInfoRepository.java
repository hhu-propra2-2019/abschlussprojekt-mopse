package mops.persistence;

import mops.persistence.file.FileInfo;
import mops.utils.AggregateBuilder;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
@AggregateBuilder
public interface FileInfoRepository extends CrudRepository<FileInfo, Long> {

    /**
     * @param dirId directory id
     * @return a list of files in that directory
     */
    @Query("SELECT * FROM file_info WHERE directory_id = :dirId")
    List<FileInfo> findAllInDirectory(@Param("dirId") long dirId);

    /**
     * Counts the total number of bytes used in a group.
     *
     * @param groupId group id
     * @return total storage usage in bytes
     */
    @Query("SELECT SUM(size) FROM file_info WHERE group_id = :groupId")
    long getStorageUsage(@Param("groupId") long groupId);

    /**
     * Counts the total number of bytes in all groups.
     *
     * @return total storage usage in bytes
     */
    @Query("SELECT SUM(size) FROM file_info")
    long getStorageUsage();

    /**
     * Counts the total number of files in a group.
     *
     * @param groupId group id
     * @return total file count
     */
    @Query("SELECT COUNT(*) FROM file_info WHERE group_id = :groupId")
    long getFileCount(@Param("groupId") long groupId);

    /**
     * Fetches all file info ids.
     *
     * @return all ids
     */
    @Query("SELECT id FROM file_info")
    Set<Long> findAllIds();

}
