package mops.persistence.file;

import mops.SpringTestContext;
import mops.persistence.DirectoryPermissionsRepository;
import mops.persistence.DirectoryRepository;
import mops.persistence.FileInfoRepository;
import mops.persistence.directory.Directory;
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
class FileInfoTest {

    @Autowired
    private FileInfoRepository repo;
    @Autowired
    private DirectoryPermissionsRepository permRepo;
    @Autowired
    private DirectoryRepository dirRepo;

    private FileInfo file;

    @BeforeEach
    void setup() {
        DirectoryPermissions rootDirPerms = new DirectoryPermissions(Set.of(new DirectoryPermissionEntry("admin", true,
                true, true)));
        rootDirPerms = permRepo.save(rootDirPerms);

        Directory rootDir = new Directory("", null, -1, rootDirPerms.getId());
        rootDir = dirRepo.save(rootDir);

        FileTag t1 = new FileTag("1");
        FileTag t2 = new FileTag("2");
        this.file = new FileInfo("a", rootDir.getId(), "txt", 0, "a", Set.of(t1, t2));
    }

    @Test
    void failCreation() {
        assertThatThrownBy(() -> new FileTag(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new FileInfo(null, -1, null, 0, null, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void failSave() {
        FileInfo wrong = new FileInfo("a", -1, "txt", 0, "a", Set.of());

        assertThatThrownBy(() -> repo.save(wrong))
                .isInstanceOf(DbActionExecutionException.class);
    }

    @Test
    void save() {
        FileInfo saved = repo.save(file);

        assertThat(saved).isEqualToIgnoringNullFields(file);
    }

    @Test
    void loadSave() {
        Long id = repo.save(file).getId();

        Optional<FileInfo> loaded = repo.findById(id);

        assertThat(loaded).get().isEqualToIgnoringNullFields(file);
    }

    @Test
    void loadWriteSave() {
        Long id = repo.save(file).getId();
        FileInfo loaded = repo.findById(id).orElseThrow();

        loaded.setName("b");

        Long id2 = repo.save(loaded).getId();

        Optional<FileInfo> loaded2 = repo.findById(id2);

        assertThat(loaded2).get().isEqualTo(loaded);
    }
}
