package mops.persistence.permission;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

/**
 * Represents specific access rights for a role.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class DirectoryPermissionEntry {

    /**
     * Database Id.
     */
    @Id
    private Long id;
    /**
     * The role this belongs to.
     */
    private String role;
    /**
     * If the role may read directories.
     */
    private boolean canRead;
    /**
     * If the role may upload files.
     */
    private boolean canWrite;
    /**
     * If this role may delete files.
     */
    private boolean canDelete;

    /**
     * Create a new DirectoryPermissionEntry.
     *
     * @param role      role
     * @param canRead   can read
     * @param canWrite  cam write/upload
     * @param canDelete can delete
     */
    DirectoryPermissionEntry(String role, boolean canRead, boolean canWrite, boolean canDelete) {
        this.role = role;
        this.canRead = canRead;
        this.canWrite = canWrite;
        this.canDelete = canDelete;
    }
}
