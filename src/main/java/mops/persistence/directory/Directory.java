package mops.persistence.directory;

import lombok.*;
import mops.utils.AggregateRoot;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.PersistenceConstructor;

import java.sql.Timestamp;
import java.time.Instant;

/**
 * Represents a directory where files can be stored.
 */
@Data
@AllArgsConstructor(access = AccessLevel.PACKAGE, onConstructor_ = @PersistenceConstructor)
@AggregateRoot
public class Directory {

    /**
     * Database Id.
     */
    @Id
    @Setter(AccessLevel.PRIVATE)
    private Long id;
    /**
     * Directory name.
     */
    @NonNull
    private String name;
    /**
     * Id of the Directory above this one.
     */
    private Long parentId;
    /**
     * Id of the group which this Directory belongs to.
     */
    private long groupOwner;
    /**
     * Id of the DirectoryPermissions object which stores the access permission for this Directory tree.
     */
    private long permissionsId;
    /**
     * Creation Time.
     */
    @Setter(AccessLevel.PRIVATE)
    @CreatedDate
    private Timestamp creationTime;
    /**
     * Last Modified Time.
     */
    @Setter(AccessLevel.PRIVATE)
    @LastModifiedDate
    private Timestamp lastModifiedTime;

    /**
     * Create a new Directory.
     *
     * @param name          Directory name
     * @param parentId      Id of parent Directory
     * @param groupOwner    Id of the owning group
     * @param permissionsId Id of the DirectoryPermissions
     */
    public Directory(String name, Long parentId, long groupOwner, long permissionsId) {
        this(null, name, parentId, groupOwner, permissionsId, null, null);
    }

    /**
     * Get the creation time.
     *
     * @return creation time
     */
    public Instant getCreationTime() {
        return creationTime == null ? Instant.EPOCH : creationTime.toInstant();
    }

    /**
     * Get the last modified time.
     *
     * @return last modified time
     */
    public Instant getLastModifiedTime() {
        return lastModifiedTime == null ? Instant.EPOCH : lastModifiedTime.toInstant();
    }
}
