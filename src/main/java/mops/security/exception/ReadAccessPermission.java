package mops.security.exception;

import mops.exception.MopsException;

public class ReadAccessPermission extends MopsException {
    /**
     * Is thrown when an user tries to read something which he/she/it does not have the permission for.
     *
     * @param message message of the checked exception
     */
    public ReadAccessPermission(String message) {
        super(message);
    }
}
