package mops.presentation.error;

import lombok.Value;
import org.springframework.lang.NonNull;

/**
 * An error to be shown in the web template - based on an exception message.
 */
@Value
// @Value automatically makes all fields `private final` which CheckStyle and PMD don't see
@SuppressWarnings({ "checkstyle:VisibilityModifier", "PMD.DefaultPackage" })
public class MessagePresentationError implements PresentationError {

    /**
     * The message.
     */
    @lombok.NonNull
    String message;

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public String getMessage() {
        return message;
    }
}
