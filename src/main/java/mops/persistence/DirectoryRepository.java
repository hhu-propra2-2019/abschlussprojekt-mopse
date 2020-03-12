package mops.persistence;

import mops.persistence.directory.Directory;
import mops.utils.AggregateBuilder;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@AggregateBuilder
public interface DirectoryRepository extends CrudRepository<Directory, Long> {
}
