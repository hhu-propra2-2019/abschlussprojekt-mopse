package mops.businesslogic.exception;

import mops.exception.MopsException;

/**
 * Throws if something went wrong with fetching information from the database.
 */
public class DatabaseException extends MopsException {

    /**
     * Throws if something went wrong with fetching information from the database.
     *
     * @param message explanation of the error
     */
    public DatabaseException(String message) {
        super(message);
    }

    /**
     * Throws if something went wrong with fetching information from the database.
     *
     * @param message explanation of the error
     * @param cause   wrapped exception
     */
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
