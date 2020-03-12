package mops.businesslogic;

import mops.persistence.file.FileInfo;

/**
 * Object that contains query parameter for searching files in a group.
 */
public interface FileQuery {
    /**
     * @param file a file information object
     * @return if the file meta data matches the query request
     */
    boolean checkMatch(FileInfo file);
}