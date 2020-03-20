package mops.businesslogic.exception;

import mops.exception.MopsException;

/**
 * Throws if file is not found.
 */
public class FileNotFoundException extends MopsException {

    /**
     * Throws if file is not found.
     *
     * @param message explanation of the error
     */
    public FileNotFoundException(String message) {
        super(message);
    }

    /**
     * Throws if file is not found.
     *
     * @param message explanation of the error
     * @param cause   wrapped exception
     */
    public FileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
