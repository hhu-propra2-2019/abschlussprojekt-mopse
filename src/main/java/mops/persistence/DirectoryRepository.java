package mops.persistence;

import mops.persistence.directory.Directory;
import mops.util.AggregateBuilder;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Database connection for directories.
 */
@Repository
@AggregateBuilder
public interface DirectoryRepository extends CrudRepository<Directory, Long> {

    /**
     * Gets all sub folders.
     *
     * @param parentId the id of the parent folder
     * @return a list of level one sub folder of the parent folder
     */
    @Query("SELECT * FROM directory WHERE parent_id = :parentId")
    List<Directory> getAllSubFoldersOfParent(@Param("parentId") long parentId);

    /**
     * Gets folder count in a group.
     *
     * @param groupId the group id
     * @return the number of the folders the group already has
     */
    @Query("SELECT COALESCE(COUNT(*), 0) FROM directory WHERE group_owner = :groupId")
    long getDirCountInGroup(@Param("groupId") long groupId);

    /**
     * Gets the root folder of a group.
     *
     * @param groupId the id of group
     * @return the group directory
     */
    @Query("SELECT * FROM directory WHERE group_owner = :groupId AND parent_id IS NULL")
    Optional<Directory> getRootFolder(@Param("groupId") long groupId);

}
