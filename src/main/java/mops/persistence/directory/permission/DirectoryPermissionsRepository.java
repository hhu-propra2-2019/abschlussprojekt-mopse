package mops.persistence.directory.permission;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DirectoryPermissionsRepository extends CrudRepository<DirectoryPermissions, Long> {
}
