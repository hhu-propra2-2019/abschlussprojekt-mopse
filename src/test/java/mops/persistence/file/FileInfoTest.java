package mops.persistence.file;

import mops.persistence.DirectoryPermissionsRepository;
import mops.persistence.DirectoryRepository;
import mops.persistence.FileInfoRepository;
import mops.persistence.directory.Directory;
import mops.persistence.permission.DirectoryPermissions;
import mops.utils.DbContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DbContext
@DataJdbcTest
class FileInfoTest {

    @Autowired
    FileInfoRepository repo;
    @Autowired
    DirectoryPermissionsRepository permRepo;
    @Autowired
    DirectoryRepository dirRepo;

    FileInfo file;
    Directory rootDir;

    @BeforeEach
    void setup() {
        DirectoryPermissions rootDirPerms = DirectoryPermissions.builder()
                .entry("admin", true, true, true)
                .build();
        rootDirPerms = permRepo.save(rootDirPerms);

        this.rootDir = Directory.builder()
                .name("")
                .groupOwner(0L)
                .permissions(rootDirPerms)
                .build();
        rootDir = dirRepo.save(rootDir);

        this.file = FileInfo.builder()
                .name("a")
                .directory(rootDir)
                .type("txt")
                .size(0L)
                .owner("user")
                .tag("1")
                .tag("2")
                .build();
    }

    @Test
    void failCreation() {
        assertThatThrownBy(() -> FileInfo.builder().build())
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> FileInfo.builder().name(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void failSave() {
        FileInfo wrong = FileInfo.builder()
                .name("a")
                .directory(0L)
                .type("txt")
                .size(0L)
                .owner("user")
                .build();

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
        FileInfo saved = repo.save(file);

        Optional<FileInfo> loaded = repo.findById(saved.getId());

        assertThat(loaded).get().isEqualTo(saved);
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

    @Test
    void fetchAllIds() {
        //clear all
        repo.deleteAll();

        FileInfo f1 = FileInfo.builder()
                .name("a")
                .directory(rootDir)
                .type("txt")
                .size(0L)
                .owner("user")
                .tag("1")
                .tag("2")
                .build();

        FileInfo f2 = FileInfo.builder()
                .name("b")
                .directory(rootDir)
                .type("txt")
                .size(0L)
                .owner("user")
                .tag("1")
                .tag("2")
                .build();

        FileInfo f3 = FileInfo.builder()
                .name("c")
                .directory(rootDir)
                .type("txt")
                .size(0L)
                .owner("user")
                .tag("1")
                .tag("2")
                .build();

        f1 = repo.save(f1);
        f2 = repo.save(f2);
        f3 = repo.save(f3);

        Set<Long> fetchedIds1 = repo.findAllIds();

        assertThat(fetchedIds1).containsExactlyInAnyOrder(
                f1.getId(),
                f2.getId(),
                f3.getId()
        );

        repo.delete(f2);
        Set<Long> fetchedIds2 = repo.findAllIds();

        assertThat(fetchedIds2).containsExactlyInAnyOrder(
                f1.getId(),
                f3.getId()
        );
    }
}
