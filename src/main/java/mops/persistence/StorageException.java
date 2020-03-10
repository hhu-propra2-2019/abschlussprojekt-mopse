package mops.persistence;

@SuppressWarnings("PMD.MissingSerialVersionUID")
public class StorageException extends Exception {
    /**
     * Custom exception for everything while handling with the File Storage.
     *
     * @param exc original exception
     */
    public StorageException(Exception exc) {
        super(exc);
    }
}
