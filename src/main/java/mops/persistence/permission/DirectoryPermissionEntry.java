package mops.persistence.permission;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Represents specific access rights for a role.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DirectoryPermissionEntry {

    /**
     * The role this belongs to.
     */
    @NonNull
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

}
