package mops.persistence;

import mops.exception.MopsException;

/**
 * Custom Exception for errors in the MinIO File Storage.
 */
@SuppressWarnings("PMD.MissingSerialVersionUID")
class StorageException extends MopsException {

    /**
     * Create a new StorageException.
     *
     * @param message message
     */
    StorageException(String message) {
        super(message);
    }

    /**
     * Create a new StorageException.
     *
     * @param message message
     * @param cause   wrapped exception
     */
    StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
