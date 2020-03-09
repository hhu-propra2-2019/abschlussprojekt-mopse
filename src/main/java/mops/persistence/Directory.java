package mops.persistence;

/**
 * Represents a 'directory' of the file server.
 */
public interface Directory {
    /**
     * @return  Returns unique directory id.
     */
    int getId();
    /**
     * @return Returns display name of directory.
     */
    String getDirectoryName();
}
