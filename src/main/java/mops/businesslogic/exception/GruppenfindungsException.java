package mops.businesslogic.exception;

import mops.exception.MopsException;

/**
 * Is thrown when something happened during an API request to Gruppenfindung.
 */
public class GruppenfindungsException extends MopsException {

    /**
     * Is thrown when something happened during API request to Gruppenfindung.
     *
     * @param message what happened
     */
    public GruppenfindungsException(String message) {
        super(message);
    }

    /**
     * Is thrown when something happened during API request to Gruppenfindung.
     *
     * @param message what happened
     * @param cause   wrapped exception
     */
    public GruppenfindungsException(String message, Throwable cause) {
        super(message, cause);
    }
}
