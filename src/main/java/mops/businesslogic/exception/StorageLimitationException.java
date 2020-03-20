package mops.businesslogic.exception;

import mops.exception.MopsException;

/**
 * Is thrown we a storage limitation is violated.
 */
public class StorageLimitationException extends MopsException {

    /**
     * Is thrown we a storage limitation is violated.
     *
     * @param message message
     */
    public StorageLimitationException(String message) {
        super(message);
    }
}
