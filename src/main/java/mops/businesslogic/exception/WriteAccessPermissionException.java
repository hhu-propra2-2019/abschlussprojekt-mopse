package mops.businesslogic.exception;

import mops.exception.MopsException;

/**
 * Is thrown when an user tries to write to a directory for which he/she/it does not have writing permissions.
 */
public class WriteAccessPermissionException extends MopsException {
    /**
     * Is thrown when an user tries to write to a directory for which he/she/it does not have writing permissions.
     *
     * @param errorMessage message of the checked exception.
     */
    public WriteAccessPermissionException(String errorMessage) {
        super(errorMessage);
    }
}
