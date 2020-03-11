package mops.security;

public class DeleteAccessPermission extends Throwable {
    /**
     * Is thrown when an user tries to delete something where he/she/it doesn't have the permission for it.
     *
     * @param message message of the checked exception
     */
    public DeleteAccessPermission(String message) {
        super(message);
    }
}
