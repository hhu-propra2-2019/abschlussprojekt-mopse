package mops.businesslogic;

import lombok.AllArgsConstructor;
import lombok.Data;
import mops.persistence.permission.DirectoryPermissionEntry;

/**
 * Represents role defined by GruppenFindung.
 */
@Data
@AllArgsConstructor
public class GroupRole {
    /**
     * Name of the role. It's given by GruppenFindung.
     */
    private final String roleName;

    /**
     * @return a permission entry for that role.
     */
    public DirectoryPermissionEntry getPermissionEntry() {
        return null;
    }
}

