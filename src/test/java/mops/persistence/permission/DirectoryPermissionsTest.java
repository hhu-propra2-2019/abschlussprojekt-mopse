package mops.persistence.permission;

import mops.SpringTestContext;
import mops.persistence.DirectoryPermissionsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringTestContext
@DataJdbcTest
class DirectoryPermissionsTest {

    @Autowired
    private DirectoryPermissionsRepository repo;

    @Test
    void save() {
        DirectoryPermissionEntry e1 = new DirectoryPermissionEntry("admin", true, true, true);
        DirectoryPermissionEntry e2 = new DirectoryPermissionEntry("user", true, false, false);
        DirectoryPermissions perms = new DirectoryPermissions(Set.of(e1, e2));

        DirectoryPermissions saved = repo.save(perms);

        assertThat(saved).isEqualToIgnoringNullFields(perms);
    }

    @Test
    void loadSave() {
        DirectoryPermissionEntry e1 = new DirectoryPermissionEntry("admin", true, true, true);
        DirectoryPermissionEntry e2 = new DirectoryPermissionEntry("user", true, false, false);
        DirectoryPermissions perms = new DirectoryPermissions(Set.of(e1, e2));

        Long id = repo.save(perms).getId();

        Optional<DirectoryPermissions> loaded = repo.findById(id);

        assertThat(loaded).get().isEqualToIgnoringNullFields(perms);
    }

    @Test
    void loadWriteSave() {
        DirectoryPermissionEntry e1 = new DirectoryPermissionEntry("admin", true, true, true);
        DirectoryPermissionEntry e2 = new DirectoryPermissionEntry("user", true, false, false);
        DirectoryPermissions perms1 = new DirectoryPermissions(Set.of(e1, e2));

        Long id1 = repo.save(perms1).getId();

        DirectoryPermissionEntry e3 = new DirectoryPermissionEntry("admin", true, true, true);
        DirectoryPermissionEntry e4 = new DirectoryPermissionEntry("user", true, true, true);
        DirectoryPermissions perms2 = new DirectoryPermissions(id1, Set.of(e3, e4));

        Long id2 = repo.save(perms2).getId();

        Optional<DirectoryPermissions> loaded = repo.findById(id2);

        assertThat(loaded).get().isEqualToIgnoringNullFields(perms2);
    }
}
