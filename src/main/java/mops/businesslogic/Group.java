package mops.businesslogic;

/**
 * Represents a group (course, study group, etc.).
 */
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
