package mops.persistence;

import mops.persistence.file.FileInfo;
import mops.util.AggregateBuilder;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Database connection for file meta data.
 */
@Repository
@AggregateBuilder
public interface FileInfoRepository extends CrudRepository<FileInfo, Long> {

    /**
     * Gets all files from one directory.
     *
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
    @Query("SELECT COALESCE(SUM(size), 0) FROM file_info "
            + "INNER JOIN directory "
            + "ON file_info.directory_id = directory.id "
            + "WHERE group_owner = :groupId")
    long getStorageUsageInGroup(@Param("groupId") long groupId);

    /**
     * Counts the total number of bytes in all groups.
     *
     * @return total storage usage in bytes
     */
    @Query("SELECT COALESCE(SUM(size), 0) FROM file_info")
    long getTotalStorageUsage();

    /**
     * Counts the total number of files in a group.
     *
     * @param groupId group id
     * @return total file count
     */
    @Query("SELECT COALESCE(COUNT(*), 0) FROM file_info "
            + "INNER JOIN directory "
            + "ON file_info.directory_id = directory.id "
            + "WHERE group_owner = :groupId")
    long getFileCountInGroup(@Param("groupId") long groupId);

    /**
     * Fetches all file info ids.
     *
     * @return all ids
     */
    @Query("SELECT id FROM file_info")
    Set<Long> findAllIds();

    /**
     * Finds all FileInfo IDs, where no dir exists anymore.
     * Should theoretically always be an empty list.
     *
     * @return all IDs of found orphans
     */
    @Query("SELECT id from file_info "
            + "WHERE directory_id NOT IN "
            + "(SELECT id from directory)")
    Set<Long> findAllOrphansByDirectory();
}
