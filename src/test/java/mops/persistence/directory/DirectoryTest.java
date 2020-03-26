package mops.persistence.directory;

import mops.persistence.DirectoryPermissionsRepository;
import mops.persistence.DirectoryRepository;
import mops.persistence.permission.DirectoryPermissions;
import mops.util.AuditingDbContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@AuditingDbContext
@DataJdbcTest
class DirectoryTest {

    static final long GROUP_ID = 1L;

    @Autowired
    DirectoryRepository repo;
    @Autowired
    DirectoryPermissionsRepository permRepo;

    Directory dir;

    @BeforeEach
    void setup() {
        DirectoryPermissions rootDirPerms = DirectoryPermissions.builder()
                .entry("admin", true, true, true)
                .build();
        rootDirPerms = permRepo.save(rootDirPerms);

        Directory rootDir = Directory.builder()
                .name("")
                .groupOwner(GROUP_ID)
                .permissions(rootDirPerms)
                .build();
        rootDir = repo.save(rootDir);

        this.dir = Directory.builder()
                .fromParent(rootDir)
                .name("a")
                .build();
    }

    @Test
    void failCreation() {
        assertThatThrownBy(() -> Directory.builder().build())
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> Directory.builder().name(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void failSave() {
        Directory wrong = Directory.builder()
                .name("")
                .parent(0L)
                .groupOwner(GROUP_ID)
                .permissions(0L)
                .build();

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

        assertThat(loaded).get().isEqualTo(saved);
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
