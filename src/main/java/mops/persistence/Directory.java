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
     * @return Returns display name of directory.
     */
    String getDirectoryName();
}
