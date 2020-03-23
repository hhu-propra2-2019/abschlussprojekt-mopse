package mops.persistence.permission;

import lombok.*;
import mops.util.AggregateRoot;
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
     * The permission entries.
     */
    @NonNull
    @MappedCollection(idColumn = "permissions_id")
    private Set<DirectoryPermissionEntry> permissions;
    /**
     * Creation Time.
     */
    @Setter(AccessLevel.PRIVATE)
    @EqualsAndHashCode.Exclude
    @CreatedDate
    private Timestamp creationTime;
    /**
     * Last Modified Time.
     */
    @Setter(AccessLevel.PRIVATE)
    @EqualsAndHashCode.Exclude
    @LastModifiedDate
    private Timestamp lastModifiedTime;

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

    /**
     * Checks if a role has writing access.
     *
     * @param userRole role of the user in group
     * @return boolean if user is allowed to write
     */
    @SuppressWarnings("PMD.LawOfDemeter") //this is a stream
    public boolean isAllowedToWrite(String userRole) {
        return permissions.stream()
                .filter(DirectoryPermissionEntry::isCanWrite)
                .map(DirectoryPermissionEntry::getRole)
                .anyMatch(userRole::equals);
    }

    /**
     * Checks if a role has reading access.
     *
     * @param userRole role of the user in group
     * @return boolean if user is allowed
     */
    @SuppressWarnings("PMD.LawOfDemeter") //this is a stream
    public boolean isAllowedToRead(String userRole) {
        return permissions.stream()
                .filter(DirectoryPermissionEntry::isCanRead)
                .map(DirectoryPermissionEntry::getRole)
                .anyMatch(userRole::equals);
    }

    /**
     * Checks if a role has deleting access.
     *
     * @param userRole role of the user in group
     * @return boolean if user is allowed to delete
     */
    @SuppressWarnings("PMD.LawOfDemeter") //this is a stream
    public boolean isAllowedToDelete(String userRole) {
        return permissions.stream()
                .filter(DirectoryPermissionEntry::isCanDelete)
                .map(DirectoryPermissionEntry::getRole)
                .anyMatch(userRole::equals);
    }

    /**
     * Returns DirectoryPermissionsBuilder.
     *
     * @return DirectoryPermissionsBuilder
     */
    public static DirectoryPermissionsBuilder builder() {
        return new DirectoryPermissionsBuilder();
    }
}
