package mops.security.exception;

public class WriteAccessPermission extends Exception {
    private static final long serialVersionUID = 588335701190058578L;

    /**
     * Is thrown when a user tries to write to a directory for which he/she/it does not have writing permissions.
     *
     * @param errorMessage message of the checked exception.
     */
    public WriteAccessPermission(String errorMessage) {
        super(errorMessage);
    }
}
