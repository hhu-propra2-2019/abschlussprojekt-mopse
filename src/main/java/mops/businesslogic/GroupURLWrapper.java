package mops.businesslogic;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

/**
 * Ignore unused private field warning
 */
@AllArgsConstructor
public class GroupURLWrapper {
    /**
     * The id of the group.
     */
    @JsonProperty("group_id")
    private final long groupId;

    /**
     * The relative url of the group root dir.
     */
    @JsonGetter("url")
    public String getGroupUrl() {
        return String.format("/dir/%d", groupId);
    }
}
