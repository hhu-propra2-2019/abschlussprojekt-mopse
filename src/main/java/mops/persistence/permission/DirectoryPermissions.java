package mops.persistence.permission;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@NoArgsConstructor
@AggregateRoot
public class DirectoryPermissions {

    /**
     * Id in database.
     */
    @Id
    private Long id;
    /**
     * The permission entries.
     */
    @NonNull
    @MappedCollection(idColumn = "permissions_id")
    private Set<DirectoryPermissionEntry> entries;

    /**
     * Create new DirectoryPermissions.
     *
     * @param entries the permissions
     */
    public DirectoryPermissions(Set<DirectoryPermissionEntry> entries) {
        this.entries = entries;
    }
}
