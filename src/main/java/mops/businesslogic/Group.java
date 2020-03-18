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
    long id;

    /**
     * group name
     */
    String name;

}
