package mops.persistence;

/**
 * Represents a 'directory' of the file server.
 */
public interface Directory {
    /**
     * @return unique directory id
     */
    long getId();

    /**
     * @return display name
     */
    String getDirectoryName();
}
