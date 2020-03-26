package mops.businesslogic.gruppenfindung;

import lombok.Data;

import java.util.List;

/**
 * Updated groups since the last time stamp.
 */
@Data
public class UpdatedGroupsDTO {

    /**
     * Current event timestamp id.
     */
    private long eventId;
    /**
     * List of groups.
     */
    private List<GroupDTO> groupDAOs;

}
