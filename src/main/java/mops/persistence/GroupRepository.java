package mops.persistence;

import mops.persistence.group.Group;
import mops.util.AggregateBuilder;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Database connection for groups.
 */
@Repository
@AggregateBuilder
public interface GroupRepository extends CrudRepository<Group, Long> {
}
