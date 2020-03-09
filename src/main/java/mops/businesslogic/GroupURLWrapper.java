package mops.businesslogic;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("PMD")
public class GroupURLWrapper {
    /**
     * The id of the group.
     */
    @JsonProperty("group_id")
    private final long groupId;
    /**
     * The relative url of the group root dir.
     */
    @JsonProperty("url")
    private final String url;


    /**
     * @param groupId the of the group
     */
    public GroupURLWrapper(long groupId) {
        this.groupId = groupId;
        url = String.format("/dir/%d", groupId);
    }
}
