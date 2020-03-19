package mops.businesslogic;

import lombok.Value;

/**
 * Represents a group (course, study group, etc.).
 */
@Value
public class Group {

    /**
     * Eatabase id.
     */
    long id;

    /**
     * Group name.
     */
    String name;

}
