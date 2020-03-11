package mops.persistence.permission;

import lombok.*;
import mops.utils.AggregateRoot;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.relational.core.mapping.MappedCollection;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Set;

/**
 * Represents a collection of Permissions for a Directory.
 */
@Data
@AllArgsConstructor(access = AccessLevel.PACKAGE, onConstructor_ = @PersistenceConstructor)
@AggregateRoot
public class DirectoryPermissions {

    /**
     * Id in database.
     */
    @Id
    @Setter(AccessLevel.PRIVATE)
    private Long id;
    /**
     * This is necessary because Spring Data JDBC is unable to UPDATE empty objects.
     */
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private boolean fixJdbcBug;
    /**
     * The permission entries.
     */
    @NonNull
    @MappedCollection(idColumn = "permissions_id")
    private Set<DirectoryPermissionEntry> permissions;
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
     * Create a new permissions object.
     *
     * @param permissions role permissions
     */
    public DirectoryPermissions(Set<DirectoryPermissionEntry> permissions) {
        this(null, permissions);
    }

    /**
     * Create a new permissions object.
     *
     * @param id          database id
     * @param permissions role permissions
     */
    DirectoryPermissions(Long id, Set<DirectoryPermissionEntry> permissions) {
        this(id, false, permissions, null, null);
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
