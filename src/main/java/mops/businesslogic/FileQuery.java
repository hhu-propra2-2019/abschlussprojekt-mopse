package mops.businesslogic;

import mops.persistence.file.FileInfo;

/**
 * Object that contains query parameter for searching files in a group.
 */
public interface FileQuery {

    /**
     * Returns a builder for file queries.
     *
     * @return a file query builder
     */
    static FileQueryBuilder builder() {
        return new FileQueryBuilder();
    }

    /**
     * @param file a file information object
     * @return if the file meta data matches the query request
     */
    boolean checkMatch(FileInfo file);

}
