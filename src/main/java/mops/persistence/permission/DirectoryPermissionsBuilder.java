package mops.persistence.permission;

import mops.utils.AggregateBuilder;

import java.util.HashSet;
import java.util.Set;

@AggregateBuilder
@SuppressWarnings({ "PMD.LawOfDemeter", "PMD.TooManyMethods", "PMD.AvoidFieldNameMatchingMethodName",
        "PMD.BeanMembersShouldSerialize" })
public class DirectoryPermissionsBuilder {

    /**
     * Permissions.
     */
    private final Set<DirectoryPermissionEntry> entries = new HashSet<>();

    /**
     * Initialize from existing DirectoryPermissions.
     *
     * @param permissions existing DirectoryPermissions
     * @return this
     */
    public DirectoryPermissionsBuilder from(DirectoryPermissions permissions) {
        permissions.getPermissions().forEach(e -> entry(e.getRole(), e.isCanRead(), e.isCanWrite(), e.isCanDelete()));
        return this;
    }

    /**
     * Add permission.
     *
     * @param role      the user role
     * @param canRead   can read
     * @param canWrite  can write
     * @param canDelete can delete
     * @return this
     */
    public DirectoryPermissionsBuilder entry(String role, boolean canRead, boolean canWrite, boolean canDelete) {
        this.entries.add(new DirectoryPermissionEntry(role, canRead, canWrite, canDelete));
        return this;
    }

    /**
     * Builds the FileInfo.
     *
     * @return composed FileInfo
     * @throws IllegalStateException if DirectoryPermissionsBuilder is not complete
     */
    public DirectoryPermissions build() {
        return new DirectoryPermissions(null, false, entries, null, null);
    }
}
