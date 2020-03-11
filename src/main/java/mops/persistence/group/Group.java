package mops.persistence.group;

import mops.utils.AggregateRoot;

/**
 * Represents a group (course, study group, etc.).
 */
@AggregateRoot
public interface Group {

    /**
     * @return database id
     */
    Long getId();

    /**
     * @return group name
     */
    String getName();

    /**
     * @return group root dir
     */
    long getRootDir();

}
