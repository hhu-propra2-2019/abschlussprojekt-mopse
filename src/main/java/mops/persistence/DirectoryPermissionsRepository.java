package mops.persistence;

import mops.persistence.permission.DirectoryPermissions;
import mops.utils.AggregateBuilder;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@AggregateBuilder
public interface DirectoryPermissionsRepository extends CrudRepository<DirectoryPermissions, Long> {
}
