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

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringTestContext
@DataJdbcTest
class FileInfoTest {

    @Autowired
    private FileInfoRepository repo;
    @Autowired
    private DirectoryPermissionsRepository permRepo;
    @Autowired
    private DirectoryRepository dirRepo;

    private DirectoryPermissions rootDirPerms;
    private Directory rootDir;

    @BeforeEach
    void setup() {
        DirectoryPermissions rootDirPerms = new DirectoryPermissions(Set.of(new DirectoryPermissionEntry("admin", true,
                true, true)));
        this.rootDirPerms = permRepo.save(rootDirPerms);

        Directory rootDir = new Directory("", null, -1, rootDirPerms.getId());
        this.rootDir = dirRepo.save(rootDir);
    }

    @Test
    void save() {
        FileTag t1 = new FileTag("1");
        FileTag t2 = new FileTag("2");
        FileInfo file = new FileInfo("a", rootDir.getId(), "txt", 0, "a", Set.of(t1, t2));

        FileInfo saved = repo.save(file);

        assertThat(saved).isEqualToIgnoringNullFields(file);
    }

    @Test
    void loadSave() {
        FileTag t1 = new FileTag("1");
        FileTag t2 = new FileTag("2");
        FileInfo file = new FileInfo("a", rootDir.getId(), "txt", 0, "a", Set.of(t1, t2));

        Long id = repo.save(file).getId();

        Optional<FileInfo> loaded = repo.findById(id);

        assertThat(loaded).get().isEqualToIgnoringNullFields(file);
    }

    @Test
    void loadWriteSave() {
        FileTag t1 = new FileTag("1");
        FileTag t2 = new FileTag("2");
        FileInfo file1 = new FileInfo("a", rootDir.getId(), "txt", 0, "a", Set.of(t1, t2));

        Long id1 = repo.save(file1).getId();

        FileTag t3 = new FileTag("1");
        FileTag t4 = new FileTag("2");
        FileInfo file2 = new FileInfo(id1, "b", rootDir.getId(), "txt", 0, "a", Set.of(t3, t4));

        Long id2 = repo.save(file2).getId();

        Optional<FileInfo> loaded = repo.findById(id2);

        assertThat(loaded).get().isEqualToIgnoringNullFields(file2);
    }
}
