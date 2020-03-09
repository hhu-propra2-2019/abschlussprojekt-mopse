package mops.businesslogic;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@SuppressWarnings("PMD")
@JsonAutoDetect
public class GroupURLWrapper {
    /**
     * The id of the group.
     */
    private final long groupId;
    /**
     * The relative url of the group root dir.
     */
    private final String url;


    /**
     * @param groupId the of the group
     */
    public GroupURLWrapper(long groupId) {
        this.groupId = groupId;
        url = String.format("/dir/%d", groupId);
    }
}
