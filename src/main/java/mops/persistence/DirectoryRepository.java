package mops.persistence;

import mops.persistence.directory.Directory;
import mops.utils.AggregateBuilder;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@AggregateBuilder
public interface DirectoryRepository extends CrudRepository<Directory, Long> {

    /**
     * @param parentId the id of the parent folder
     * @return a list of level one sub folder of the parent folder
     */
    @Query("SELECT * FROM directory WHERE parent_id IS NOT NULL AND parent_id = :parentId")
    List<Directory> getAllSubFoldersOfParent(@Param("parentId") long parentId);

    /**
     * @param groupOwner the group od
     * @return the number of the folders the group already has
     */
    @Query("SELECT COUNT(*) FROM directory WHERE group_owner = :groupOwner")
    long getGroupFolderCount(@Param("groupOwner") long groupOwner);

    /**
     * @param groupId the id of group
     * @return the group directory
     */
    @Query("SELECT * FROM directory WHERE id = :groupId AND parent_id IS NULL")
    Optional<Directory> getRootFolder(@Param("groupId") long groupId);

}
