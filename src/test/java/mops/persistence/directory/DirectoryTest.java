package mops.persistence.directory;

import mops.persistence.DirectoryPermissionsRepository;
import mops.persistence.DirectoryRepository;
import mops.persistence.permission.DirectoryPermissions;
import mops.utils.DbContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DbContext
@DataJdbcTest
class DirectoryTest {

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
