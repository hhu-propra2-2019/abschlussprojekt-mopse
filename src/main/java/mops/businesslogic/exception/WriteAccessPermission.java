package mops.businesslogic.exception;

import mops.exception.MopsException;

public class WriteAccessPermission extends MopsException {
    /**
     * Is thrown when an user tries to write to a directory for which he/she/it does not have writing permissions.
     *
     * @param errorMessage message of the checked exception.
     */
    public WriteAccessPermission(String errorMessage) {
        super(errorMessage);
    }
}
