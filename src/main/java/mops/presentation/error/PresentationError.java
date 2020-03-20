package mops.presentation.error;

import org.springframework.lang.NonNull;

/**
 * An error to be shown in the web template.
 */
public interface PresentationError {

    /**
     * Get the error message.
     *
     * @return error message
     */
    @NonNull
    String getMessage();

}
