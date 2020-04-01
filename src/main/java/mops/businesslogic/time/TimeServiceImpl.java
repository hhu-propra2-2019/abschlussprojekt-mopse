package mops.businesslogic.time;

import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Queries the time.
 */
@Service
public class TimeServiceImpl implements TimeService {

    /**
     * {@inheritDoc}
     */
    @Override
    public Instant getInstantNow() {
        return Instant.now();
    }
}
