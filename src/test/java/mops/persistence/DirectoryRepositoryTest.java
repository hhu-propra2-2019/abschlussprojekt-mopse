package mops.persistence;

import mops.persistence.directory.Directory;
import mops.persistence.permission.DirectoryPermissions;
import mops.utils.AuditingDbContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@AuditingDbContext
@DataJdbcTest
class DirectoryRepositoryTest {

    static final long GROUP_ID = 0L;

    @Autowired
    DirectoryRepository directoryRepository;
    @Autowired
    DirectoryPermissionsRepository directoryPermissionsRepository;

    /**
     * Tests if sub folders are correctly returned.
     */
    @Test
    void getAllSubFoldersOfParent() {
        DirectoryPermissions empty = directoryPermissionsRepository.save(DirectoryPermissions.builder().build());
        Directory root = Directory.builder()
                .name("")
                .groupOwner(GROUP_ID)
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

    @Test
    public void groupFolderCountTest() {
        DirectoryPermissions empty = directoryPermissionsRepository.save(DirectoryPermissions.builder().build());
        Directory root = Directory.builder()
                .name("")
                .groupOwner(GROUP_ID)
                .permissions(empty)
                .build();
        directoryRepository.save(root);

        Directory a = Directory.builder()
                .fromParent(root)
                .name("a")
                .build();
        Directory b = Directory.builder()
                .fromParent(root)
                .name("b")
                .build();

        directoryRepository.saveAll(List.of(a, b));

        long groupFolderCount = directoryRepository.getDirCountInGroup(GROUP_ID);

        assertThat(groupFolderCount).isEqualTo(3L);
    }
}
