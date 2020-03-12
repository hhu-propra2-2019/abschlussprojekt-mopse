package mops.persistence;

import mops.persistence.directory.Directory;
import mops.persistence.permission.DirectoryPermissions;
import mops.utils.DbContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DbContext
@DataJdbcTest
public class DirectoryRepositoryTest {

    /**
     * Handles db communication for directories.
     */
    @Autowired
    private DirectoryRepository directoryRepository;
    /**
     * Handles db communication for directory permissions.
     */
    @Autowired
    private DirectoryPermissionsRepository directoryPermissionsRepository;

    /**
     * Tests if sub folders are correctly returned.
     */
    @Test
    public void getAllSubFoldersOfParent() {
        long groupOwner = 1L;
        long permissionsId = directoryPermissionsRepository.save(new DirectoryPermissions(Set.of())).getId();
        Directory root = new Directory("root", null, groupOwner, permissionsId);
        Directory savedRoot = directoryRepository.save(root);

        Directory first = new Directory("first", savedRoot.getId(), groupOwner, permissionsId);
        Directory second = new Directory("second", savedRoot.getId(), groupOwner, permissionsId);

        List<Directory> savedDirectories = (List<Directory>) directoryRepository.saveAll(List.of(first, second));

        List<Directory> allSubFoldersOfParent = directoryRepository.getAllSubFoldersOfParent(savedRoot.getId());

        assertThat(allSubFoldersOfParent).isEqualTo(savedDirectories);
    }
}
