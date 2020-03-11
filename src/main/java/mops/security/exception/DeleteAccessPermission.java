package mops.security.exception;

import mops.exception.MopsException;

public class DeleteAccessPermission extends MopsException {
    /**
     * Is thrown when an user tries to delete something where he/she/it doesn't have the permission for it.
     *
     * @param message message of the checked exception
     */
    public DeleteAccessPermission(String message) {
        super(message);
    }
}
