package mops.persistence.directory;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public interface DirectoryRepository extends CrudRepository<Directory, Long> {
}
