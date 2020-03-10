package mops.businesslogic;

import mops.persistence.permission.DirectoryPermissionEntry;

/**
 * Represents role defined by GruppenFindung.
 */
public interface GroupRole {
    /**
     * @return a permission entry for that role.
     */
    DirectoryPermissionEntry getPermissionEntry();
}

