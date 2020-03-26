package mops.persistence;

import mops.persistence.event.LatestEventId;
import mops.util.AggregateBuilder;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Database connection for the latest event id.
 */
@Repository
@AggregateBuilder
public interface LatestEventIdRepository extends CrudRepository<LatestEventId, Long> {
}
