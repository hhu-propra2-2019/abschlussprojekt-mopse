package mops.persistence.permission;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import mops.utils.AggregateBuilder;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@AggregateBuilder
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@SuppressWarnings({ "PMD.LawOfDemeter", "PMD.TooManyMethods", "PMD.AvoidFieldNameMatchingMethodName",
        "PMD.BeanMembersShouldSerialize" }) // this is a builder
public class DirectoryPermissionsBuilder {

    /**
     * Database Id.
     */
    private Long id;
    /**
     * Permissions.
     */
    private final Set<DirectoryPermissionEntry> entries = new HashSet<>();
    /**
     * Creation Time.
     */
    private Instant creationTime;

    /**
     * Initialize from existing DirectoryPermissions.
     *
     * @param permissions existing DirectoryPermissions
     * @return this
     */
    public DirectoryPermissionsBuilder from(DirectoryPermissions permissions) {
        this.id = permissions.getId();
        permissions.getPermissions().forEach(e -> entry(e.getRole(), e.isCanRead(), e.isCanWrite(), e.isCanDelete()));
        this.creationTime = permissions.getCreationTime();
        return this;
    }

    /**
     * Set id.
     *
     * @param id id
     * @return this
     */
    public DirectoryPermissionsBuilder id(Long id) {
        this.id = id;
        return this;
    }

    /**
     * Set id from existing DirectoryPermissions.
     *
     * @param permissions existing DirectoryPermissions
     * @return this
     */
    public DirectoryPermissionsBuilder id(DirectoryPermissions permissions) {
        this.id = permissions == null ? null : permissions.getId();
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
    public DirectoryPermissionsBuilder entry(@NonNull String role, boolean canRead, boolean canWrite,
                                             boolean canDelete) {
        if (role.isEmpty()) {
            throw new IllegalArgumentException("role must not be empty!");
        }
        this.entries.add(new DirectoryPermissionEntry(role, canRead, canWrite, canDelete));
        return this;
    }

    /**
     * Builds the DirectoryPermissions.
     *
     * @return composed DirectoryPermissions
     */
    public DirectoryPermissions build() {
        return new DirectoryPermissions(
                id,
                entries,
                creationTime == null ? null : Timestamp.from(creationTime),
                null
        );
    }
}
