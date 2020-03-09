package mops.persistence;

import mops.persistence.directory.Directory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DirectoryRepository extends CrudRepository<Directory, Long> {
}
