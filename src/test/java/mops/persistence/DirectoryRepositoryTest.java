package mops.persistence;

import mops.persistence.directory.Directory;
import mops.persistence.permission.DirectoryPermissions;
import mops.utils.DbContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DbContext
@DataJdbcTest
class DirectoryRepositoryTest {

    /**
     * Handles db communication for directories.
     */
    @Autowired
    DirectoryRepository directoryRepository;
    /**
     * Handles db communication for directory permissions.
     */
    @Autowired
    DirectoryPermissionsRepository directoryPermissionsRepository;

    /**
     * Tests if sub folders are correctly returned.
     */
    @Test
    void getAllSubFoldersOfParent() {
        DirectoryPermissions empty = DirectoryPermissions.builder()
                .build();
        empty = directoryPermissionsRepository.save(empty);
        Directory root = Directory.builder()
                .name("")
                .groupOwner(0L)
                .permissions(empty)
                .build();
        root = directoryRepository.save(root);

        Directory a = Directory.builder()
                .fromParent(root)
                .name("a")
                .build();
        a = directoryRepository.save(a);
        Directory b = Directory.builder()
                .fromParent(root)
                .name("b")
                .build();
        b = directoryRepository.save(b);

        List<Directory> allSubFoldersOfParent = directoryRepository.getAllSubFoldersOfParent(root.getId());

        assertThat(allSubFoldersOfParent).containsExactlyInAnyOrder(a, b);
    }
}
