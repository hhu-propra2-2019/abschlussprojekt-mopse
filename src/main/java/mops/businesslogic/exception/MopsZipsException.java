package mops.businesslogic.exception;

import mops.exception.MopsException;

/**
 * Is thrown if something went wrong upon zipping.
 */
public class MopsZipsException extends MopsException {
    /**
     * Is thrown if something went wrong upon zipping.
     *
     * @param message description of what happend
     * @param exception the wrapped exception
     */
    public MopsZipsException(String message, Exception exception) {
        super(message, exception);
    }
}
