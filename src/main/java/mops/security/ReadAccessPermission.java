package mops.security;

public class ReadAccessPermission extends Exception {
    /**
     * Is thrown when an user tries to read something which he/she/it does not have the permission for.
     *
     * @param message message of the checked exception
     */
    public ReadAccessPermission(String message) {
        super(message);
    }
}
