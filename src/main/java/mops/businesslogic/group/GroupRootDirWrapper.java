package mops.businesslogic.group;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

/**
 * Wraps a url for a group given a group id.
 */
@Value
// @Value automatically makes all fields `private final` which CheckStyle and PMD don't see
@SuppressWarnings({ "checkstyle:VisibilityModifier", "PMD.DefaultPackage" })
public class GroupRootDirWrapper {

    /**
     * The id of the group.
     */
    @JsonProperty("group_id")
    long groupId;

    /**
     * The id of the group's root directory.
     */
    @JsonProperty("root_dir_id")
    long rootDirId;

    /**
     * Gets the root directory url.
     *
     * @return the relative url of the group's root directory.
     */
    @JsonGetter("root_dir_url")
    public String getRootDirUrl() {
        return String.format("/material1/dir/%d", rootDirId);
    }
}
