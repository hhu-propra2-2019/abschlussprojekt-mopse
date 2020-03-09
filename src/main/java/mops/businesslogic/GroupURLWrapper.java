package mops.businesslogic;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Ignore unused private field warning
 */
@SuppressWarnings("PMD")
public class GroupURLWrapper {
    /**
     * The id of the group.
     */
    @JsonProperty("group_id")
    private final long groupId;

    /**
     * @param groupId the of the group
     */
    public GroupURLWrapper(long groupId) {
        this.groupId = groupId;

    }

    /**
     * The relative url of the group root dir.
     */
    @JsonGetter("url")
    public String getGroupUrl() {
        return String.format("/dir/%d", groupId);
    }
}
