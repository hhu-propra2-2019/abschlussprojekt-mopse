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
import org.springframework.data.relational.core.conversion.DbActionExecutionException;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringTestContext
@DataJdbcTest
class DirectoryTest {

    @Autowired
    private DirectoryRepository repo;
    @Autowired
    private DirectoryPermissionsRepository permRepo;

    private Directory dir;

    @BeforeEach
    void setup() {
        DirectoryPermissions rootDirPerms = new DirectoryPermissions(Set.of(new DirectoryPermissionEntry("admin", true,
                true, true)));
        rootDirPerms = permRepo.save(rootDirPerms);

        Directory rootDir = new Directory("", null, -1, rootDirPerms.getId());
        rootDir = repo.save(rootDir);

        this.dir = new Directory("a", rootDir.getParentId(), -1, rootDirPerms.getId());
    }

    @Test
    void failCreation() {
        assertThatThrownBy(() -> new Directory(null, -1L, -1, -1L))
                .isInstanceOf(NullPointerException.class)
                .hasNoCause();
    }

    @Test
    void failSave() {
        Directory wrong = new Directory("a", -1L, -1, -1L);

        assertThatThrownBy(() -> repo.save(wrong))
                .isInstanceOf(DbActionExecutionException.class);
    }

    @Test
    void save() {
        Directory saved = repo.save(dir);

        assertThat(saved).isEqualToIgnoringNullFields(dir);
    }

    @Test
    void loadSave() {
        Directory saved = repo.save(dir);

        Optional<Directory> loaded = repo.findById(saved.getId());

        assertThat(loaded).get().isEqualToIgnoringNullFields(saved);
    }

    @Test
    void loadWriteSave() {
        Long id = repo.save(dir).getId();
        Directory loaded = repo.findById(id).orElseThrow();

        loaded.setName("b");
        Long id2 = repo.save(loaded).getId();

        Optional<Directory> loaded2 = repo.findById(id2);

        assertThat(loaded2).get().isEqualTo(loaded);
    }
}
