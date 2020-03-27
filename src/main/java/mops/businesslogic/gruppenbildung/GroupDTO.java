package mops.businesslogic.gruppenbildung;

import lombok.Data;

import java.util.UUID;

/**
 * Group.
 */
@Data
public class GroupDTO {

    /**
     * Group id.
     */
    private UUID groupId;
    /**
     * Course.
     */
    private String course;
    /**
     * Group description.
     */
    private String groupDescription;
    /**
     * Group name.
     */
    private String groupName;
    /**
     * Group status.
     */
    private StatusDTO status;

}
