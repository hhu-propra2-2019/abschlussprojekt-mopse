package mops.businesslogic.exception;

import mops.exception.MopsException;

/**
 * Is thrown when an user tries to delete something where he/she/it doesn't have the permission for it.
 */
public class DeleteAccessPermissionException extends MopsException {
    /**
     * Is thrown when an user tries to delete something where he/she/it doesn't have the permission for it.
     *
     * @param message message of the checked exception
     */
    public DeleteAccessPermissionException(String message) {
        super(message);
    }
}
