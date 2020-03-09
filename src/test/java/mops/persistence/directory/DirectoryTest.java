package mops.persistence.directory;

import mops.SpringTestContext;
import mops.persistence.DirectoryPermissionsRepository;
import mops.persistence.DirectoryRepository;
import mops.persistence.permission.DirectoryPermissionEntry;
import mops.persistence.permission.DirectoryPermissions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringTestContext
@DataJdbcTest
class DirectoryTest {

    @Autowired
    private DirectoryRepository repo;
    @Autowired
    private DirectoryPermissionsRepository permRepo;

    private DirectoryPermissions rootDirPerms;
    private Directory rootDir;

    @BeforeEach
    void setup() {
        DirectoryPermissions rootDirPerms = new DirectoryPermissions(Set.of(new DirectoryPermissionEntry("admin", true,
                true, true)));
        this.rootDirPerms = permRepo.save(rootDirPerms);

        Directory rootDir = new Directory("", null, -1, rootDirPerms.getId());
        this.rootDir = repo.save(rootDir);
    }

    @Test
    void save() {
        Directory dir = new Directory("a", rootDir.getParentId(), -1, rootDirPerms.getId());

        Directory saved = repo.save(dir);

        assertThat(saved).isEqualToIgnoringNullFields(dir);
    }

    @Test
    void loadSave() {
        Directory dir = new Directory("a", rootDir.getParentId(), -1, rootDirPerms.getId());

        Long id = repo.save(dir).getId();

        Optional<Directory> loaded = repo.findById(id);

        assertThat(loaded).get().isEqualToIgnoringNullFields(dir);
    }

    @Test
    void loadWriteSave() {
        Directory dir1 = new Directory("a", rootDir.getParentId(), -1, rootDirPerms.getId());

        Long id1 = repo.save(dir1).getId();

        Directory dir2 = new Directory(id1, "b", rootDir.getParentId(), -1, rootDirPerms.getId());

        Long id2 = repo.save(dir2).getId();

        Optional<Directory> loaded = repo.findById(id2);

        assertThat(loaded).get().isEqualToIgnoringNullFields(dir2);
    }
}
