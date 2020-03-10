package mops.persistence.directory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import mops.persistence.permission.DirectoryPermissions;
import mops.utils.AggregateRoot;
import org.springframework.data.annotation.Id;

/**
 * Represents a directory where files can be stored.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@AggregateRoot
public class Directory {

    /**
     * Database Id.
     */
    @Id
    private Long id;
    /**
     * Directory name.
     */
    @NonNull
    private String name;
    /**
     * Id of the Directory above this one.
     */
    private Long parentId;
    /**
     * Id of the group which this Directory belongs to.
     */
    private long groupOwner;
    /**
     * Id of the DirectoryPermissions object which stores the access permission for this Directory tree.
     */
    private long permissionsId;

    /**
     * Create a new Directory.
     *
     * @param name          Directory name
     * @param parentId      Id of parent Directory
     * @param groupOwner    Id of the owing group
     * @param permissionsId Id of the DirectoryPermissions
     */
    public Directory(@NonNull String name, Long parentId, long groupOwner, long permissionsId) {
        this.name = name;
        this.parentId = parentId;
        this.groupOwner = groupOwner;
        this.permissionsId = permissionsId;
    }

    /**
     * Sets the permission id of the directory.
     *
     * @param permissions directory permissions
     */
    public void setPermission(DirectoryPermissions permissions) {
        permissionsId = permissions.getId();
    }
}
