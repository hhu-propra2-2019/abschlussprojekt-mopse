package mops.businesslogic.exception;

import mops.exception.MopsException;

/**
 * Is thrown when something happened during API request to GruppenFindung.
 */
public class GruppenFindungException extends MopsException {
    /**
     * Is thrown when something happened during API request to GruppenFindung.
     *
     * @param message what happened
     */
    public GruppenFindungException(String message) {
        super(message);
    }
}
