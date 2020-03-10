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
    private Set<DirectoryPermissionEntry> permissions;

    /**
     * Create a new permissions object.
     *
     * @param permissions role permissions
     */
    public DirectoryPermissions(@NonNull Set<DirectoryPermissionEntry> permissions) {
        this.permissions = permissions;
    }

    /**
     * Create a new permissions object.
     *
     * @param id          database id
     * @param permissions role permissions
     */
    public DirectoryPermissions(Long id, @NonNull Set<DirectoryPermissionEntry> permissions) {
        this.id = id;
        this.permissions = permissions;
    }
}
