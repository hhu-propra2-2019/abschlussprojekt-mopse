package mops.exception;

@SuppressWarnings("PMD.MissingSerialVersionUID")
public class MopsException extends Exception {

    /**
     * Create a new MopsException.
     *
     * @param message message
     */
    public MopsException(String message) {
        super(message);
    }

    /**
     * Create a new MopsException.
     *
     * @param message message
     * @param cause   wrapped exception
     */
    public MopsException(String message, Throwable cause) {
        super(message, cause);
    }
}
