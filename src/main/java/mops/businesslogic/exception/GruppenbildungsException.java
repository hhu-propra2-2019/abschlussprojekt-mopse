package mops.businesslogic.exception;

import mops.exception.MopsException;

/**
 * Is thrown when something happened during an API request to Gruppenbildung.
 */
public class GruppenbildungsException extends MopsException {

    /**
     * Is thrown when something happened during API request to Gruppenbildung.
     *
     * @param message what happened
     */
    public GruppenbildungsException(String message) {
        super(message);
    }

    /**
     * Is thrown when something happened during API request to Gruppenbildung.
     *
     * @param message what happened
     * @param cause   wrapped exception
     */
    public GruppenbildungsException(String message, Throwable cause) {
        super(message, cause);
    }
}
