package mops.businesslogic.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mops.businesslogic.exception.DatabaseException;
import mops.exception.MopsException;
import mops.persistence.LatestEventIdRepository;
import mops.persistence.event.LatestEventId;
import org.springframework.stereotype.Service;

/**
 * Service to interface with the latest event id.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LatestEventIdService {

    /**
     * Latest event id repository.
     */
    private final LatestEventIdRepository latestEventIdRepository;

    /**
     * Get the latest event id.
     *
     * @return loaded latest event id
     * @throws MopsException on error
     */
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public LatestEventId getLatestEventId() throws MopsException {
        try {
            return latestEventIdRepository.findById(0L).orElse(LatestEventId.of());
        } catch (Exception e) {
            log.error("Failed to get latest event id database:", e);
            throw new DatabaseException("Neueste Event Id konnte nicht geladen werden!", e);
        }
    }

    /**
     * Saves the given latest event id.
     *
     * @param latestEventId latest event id to save
     * @throws MopsException on error
     */
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public void saveLatestEventId(LatestEventId latestEventId) throws MopsException {
        try {
            latestEventIdRepository.save(latestEventId);
        } catch (Exception e) {
            log.error("Failed to save latest event id database:", e);
            throw new DatabaseException("Neueste Event Id konnte nicht gespeichert werden!", e);
        }
    }
}
