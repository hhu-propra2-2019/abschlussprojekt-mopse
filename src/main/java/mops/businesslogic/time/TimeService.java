package mops.businesslogic.time;

import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Queries the time.
 */
@Service
public interface TimeService {

    /**
     * Get the current Instant time.
     *
     * @return current time as Instant
     */
    Instant getInstantNow();

}
