package mops.presentation.error;

import lombok.Value;
import mops.exception.MopsException;
import org.springframework.lang.NonNull;

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
    @lombok.NonNull
    MopsException exception;

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public String getMessage() {
        String message = exception.getMessage();
        return message == null ? "null" : message;
    }
}
