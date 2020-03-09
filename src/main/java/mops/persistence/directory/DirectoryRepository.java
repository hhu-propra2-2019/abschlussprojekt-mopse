package mops.persistence.directory;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository
public interface DirectoryRepository extends CrudRepository<Directory, Long> {
}
