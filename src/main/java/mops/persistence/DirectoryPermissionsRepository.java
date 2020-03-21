package mops.persistence;

import mops.persistence.permission.DirectoryPermissions;
import mops.util.AggregateBuilder;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Database connection for directory permissions.
 */
@Repository
@AggregateBuilder
public interface DirectoryPermissionsRepository extends CrudRepository<DirectoryPermissions, Long> {
}
