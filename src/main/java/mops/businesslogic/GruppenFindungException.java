package mops.businesslogic;

import mops.exception.MopsException;

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
