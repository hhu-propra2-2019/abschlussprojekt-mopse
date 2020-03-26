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
     */
    public MopsZipsException(String message) {
        super(message);
    }
}
