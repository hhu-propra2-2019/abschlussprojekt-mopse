package mops.businesslogic;

import mops.exception.MopsException;

public class DatabaseException extends MopsException {
    /**
     * Throws if something went wrong with fetching information from the database.
     *
     * @param message explanation of the error
     */
    public DatabaseException(String message) {
        super(message);
    }
}
