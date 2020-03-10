package mops.persistence;

import mops.persistence.permission.DirectoryPermissions;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DirectoryPermissionsRepository extends CrudRepository<DirectoryPermissions, Long> {
}
