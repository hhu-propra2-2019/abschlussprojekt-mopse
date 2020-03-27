package mops.presentation.error;

import lombok.Value;

/**
 * An error to be shown in the web template - based on an exception message.
 */
@Value
// @Value automatically makes all fields `private final` which CheckStyle and PMD don't see
@SuppressWarnings({ "checkstyle:VisibilityModifier", "PMD.DefaultPackage" })
public class ExceptionPresentationError implements PresentationError {

    /**
     * The exception.
     */
    Exception exception;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        String message = exception.getMessage();
        return message == null ? "null" : message;
    }
}
