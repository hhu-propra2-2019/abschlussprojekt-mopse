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
     * @param cause   the wrapped exception
     */
    public MopsZipsException(String message, Throwable cause) {
        super(message, cause);
    }
}
