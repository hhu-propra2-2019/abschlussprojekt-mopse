package mops.businesslogic.group;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Value;
import mops.persistence.directory.Directory;

import java.util.UUID;

/**
 * Wraps a url for a group given a group id.
 */
@Value
// @Value automatically makes all fields `private final` which CheckStyle and PMD don't see
@SuppressWarnings({ "checkstyle:VisibilityModifier", "PMD.DefaultPackage" })
public class GroupRootDirWrapper {

    /**
     * The root directory.
     */
    @JsonIgnore
    Directory rootDir;

    /**
     * The id of the group.
     *
     * @return group id
     */
    @JsonGetter("group_id")
    public UUID getGroupId() {
        return rootDir.getGroupOwner();
    }

    /**
     * The id of the group's root directory.
     *
     * @return root directory id
     */
    @JsonGetter("root_dir_id")
    public long getRootDirId() {
        return rootDir.getId();
    }

    /**
     * Gets the root directory url.
     *
     * @return the relative url of the group's root directory.
     */
    @JsonGetter("root_dir_url")
    public String getRootDirUrl() {
        return String.format("/material1/dir/%d", getRootDirId());
    }
}
