package mops.persistence.directory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import mops.persistence.permission.DirectoryPermissions;
import mops.util.AggregateBuilder;

import java.sql.Timestamp;
import java.time.Instant;

/**
 * Builds directories.
 */
@Slf4j
@AggregateBuilder
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@SuppressWarnings({ "PMD.LawOfDemeter", "PMD.TooManyMethods", "PMD.AvoidFieldNameMatchingMethodName",
        "PMD.BeanMembersShouldSerialize" }) // this is a builder
public class DirectoryBuilder {

    /**
     * Database Id.
     */
    private Long id;
    /**
     * Directory name.
     */
    private String name;
    /**
     * Id of the Directory above this one.
     */
    private Long parentId;
    /**
     * Id of the group which this Directory belongs to.
     */
    private long groupOwner = -1L;
    /**
     * Id of the DirectoryPermissions object which stores the access permission for this Directory tree.
     */
    private long permissionsId = -1L;
    /**
     * Creation Time.
     */
    private Instant creationTime;

    /**
     * Initialize from existing Directory.
     *
     * @param directory existing Directory
     * @return this
     */
    public DirectoryBuilder from(@NonNull Directory directory) {
        this.id = directory.getId();
        this.name = directory.getName();
        this.parentId = directory.getParentId();
        this.groupOwner = directory.getGroupOwner();
        this.permissionsId = directory.getPermissionsId();
        this.creationTime = directory.getCreationTime();
        return this;
    }

    /**
     * Initialize from parent Directory.
     *
     * @param parent parent Directory
     * @return this
     */
    public DirectoryBuilder fromParent(@NonNull Directory parent) {
        this.parentId = parent.getId();
        this.groupOwner = parent.getGroupOwner();
        this.permissionsId = parent.getPermissionsId();
        return this;
    }

    /**
     * Set id.
     *
     * @param id id
     * @return this
     */
    public DirectoryBuilder id(Long id) {
        this.id = id;
        return this;
    }

    /**
     * Set id from existing Directory.
     *
     * @param directory existing Directory
     * @return this
     */
    public DirectoryBuilder id(Directory directory) {
        this.id = directory == null ? null : directory.getId();
        return this;
    }

    /**
     * Set name.
     *
     * @param name name
     * @return this
     */
    public DirectoryBuilder name(@NonNull String name) {
        this.name = name;
        return this;
    }

    /**
     * Set parent id.
     *
     * @param parentId is the parent directory id
     * @return this
     */
    public DirectoryBuilder parent(long parentId) {
        this.parentId = parentId;
        return this;
    }

    /**
     * Set parent.
     *
     * @param parent parent
     * @return this
     */
    public DirectoryBuilder parent(@NonNull Directory parent) {
        this.parentId = parent.getId();
        return this;
    }

    /**
     * Set owning group id.
     *
     * @param groupOwner id of owning group
     * @return this
     */
    public DirectoryBuilder groupOwner(long groupOwner) {
        this.groupOwner = groupOwner;
        return this;
    }

    /**
     * Set permissons id.
     *
     * @param permissionsId permissions id
     * @return this
     */
    public DirectoryBuilder permissions(long permissionsId) {
        this.permissionsId = permissionsId;
        return this;
    }

    /**
     * Set permissons id.
     *
     * @param permissions permissions
     * @return this
     */
    public DirectoryBuilder permissions(@NonNull DirectoryPermissions permissions) {
        this.permissionsId = permissions.getId();
        return this;
    }

    /**
     * Builds the Directory.
     *
     * @return composed Directory
     * @throws IllegalStateException if Directory is not complete
     */
    public Directory build() {
        if (name == null) {
            log.error("Directory is not completely setup name was not set.");
            throw new IllegalStateException("Directory incomplete: name must be set!");
        } else if (groupOwner == -1L) {
            log.error("Directory is not completely setup group owner was not set.");
            throw new IllegalStateException("Directory incomplete: groupOwner must be set!");
        } else if (permissionsId == -1L) {
            log.error("Directory is not completely setup permission id was not set.");
            throw new IllegalStateException("Directory incomplete: permissionsId must be set!");
        }

        return new Directory(
                id,
                name,
                parentId,
                groupOwner,
                permissionsId,
                creationTime == null ? null : Timestamp.from(creationTime),
                null
        );
    }
}
