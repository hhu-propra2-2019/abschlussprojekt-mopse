package mops.businesslogic.group;

import lombok.Value;

/**
 * Represents a group (course, study group, etc.).
 */
@Value
@SuppressWarnings({ "checkstyle:VisibilityModifier", "PMD.DefaultPackage" })
public class Group {

    /**
     * Database id.
     */
    long id;
    /**
     * Group name.
     */
    String name;

}
