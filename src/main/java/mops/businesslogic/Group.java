package mops.businesslogic;

import lombok.Value;

/**
 * Represents a group (course, study group, etc.).
 */
@Value
public class Group {

    /**
     * database id
     */
    private Long id;

    /**
     * group name
     */
    private String name;

}
