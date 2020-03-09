package mops.persistence.permission;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import mops.utils.AggregateRoot;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;

import java.util.Set;

/**
 * Represents a collection of Permissions for a Directory.
 */
@Data
@NoArgsConstructor
@AggregateRoot
public class DirectoryPermissions {

    /**
     * Id in database.
     */
    @Id
    private Long id;
    /**
     * This is necessary because Spring Data JDBC is unable to UPDATE empty objects.
     */
    private boolean fixJdbcBug;
    /**
     * The permission entries.
     */
    @NonNull
    @MappedCollection(idColumn = "permissions_id")
    private Set<DirectoryPermissionEntry> entries;

    /**
     * Create new DirectoryPermissions.
     *
     * @param entries the role permissions
     */
    public DirectoryPermissions(Set<DirectoryPermissionEntry> entries) {
        this.entries = entries;
    }

    /**
     * Create new DirectoryPermissions.
     *
     * @param id      database id
     * @param entries the role permissions
     */
    public DirectoryPermissions(Long id, Set<DirectoryPermissionEntry> entries) {
        this.id = id;
        this.entries = entries;
    }
}
