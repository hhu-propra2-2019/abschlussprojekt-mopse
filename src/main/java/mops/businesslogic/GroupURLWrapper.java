package mops.businesslogic;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

/**
 * Wraps a url for a group given a group id.
 */
@AllArgsConstructor
public class GroupURLWrapper {
    /**
     * The id of the group.
     */
    @JsonProperty("group_id")
    private final long groupId;

    /**
     * @return the relative url of the group root dir.
     */
    @JsonGetter("url")
    public String getGroupUrl() {
        return String.format("/dir/%d", groupId);
    }
}
