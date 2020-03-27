package mops.businesslogic.exception;

import mops.exception.MopsException;

/**
 * Throws if file is not found.
 */
public class EmptyNameException extends MopsException {

    /**
     * Throws if file is not found.
     *
     * @param message explanation of the error
     */
    public EmptyNameException(String message) {
        super(message);
    }

    /**
     * Throws if file is not found.
     *
     * @param message explanation of the error
     * @param cause   wrapped exception
     */
    public EmptyNameException(String message, Throwable cause) {
        super(message, cause);
    }
}
