package mops.persistence.permission;

import mops.persistence.DirectoryPermissionsRepository;
import mops.util.AuditingDbContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@AuditingDbContext
@DataJdbcTest
class DirectoryPermissionsTest {

    @Autowired
    DirectoryPermissionsRepository repo;

    DirectoryPermissions perms;

    @BeforeEach
    void setup() {
        this.perms = DirectoryPermissions.builder()
                .entry("admin", true, true, true)
                .entry("user", true, false, false)
                .build();
    }

    @Test
    void failCreation() {
        assertThatThrownBy(() -> DirectoryPermissions.builder()
                .entry(null, false, false, false))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void save() {
        DirectoryPermissions saved = repo.save(perms);

        assertThat(saved).isEqualToIgnoringNullFields(perms);
    }

    @Test
    void loadSave() {
        DirectoryPermissions saved = repo.save(perms);

        Optional<DirectoryPermissions> loaded = repo.findById(saved.getId());

        assertThat(loaded).get().isEqualTo(saved);
    }

    @Test
    void loadWriteSave() {
        Long id = repo.save(perms).getId();
        DirectoryPermissions loaded = repo.findById(id).orElseThrow();

        DirectoryPermissionEntry e1 = new DirectoryPermissionEntry("admin", true, true, true);
        DirectoryPermissionEntry e2 = new DirectoryPermissionEntry("user", true, true, true);
        loaded.setPermissions(Set.of(e1, e2));
        Long id2 = repo.save(loaded).getId();

        Optional<DirectoryPermissions> loaded2 = repo.findById(id2);

        assertThat(loaded2).get().isEqualTo(loaded);
    }

    @Test
    void copyTest() {
        DirectoryPermissions admin = DirectoryPermissions.builder()
                .entry("admin", true, true, true)
                .id(1L)
                .build();
        DirectoryPermissions copy = DirectoryPermissions.builder()
                .from(admin)
                .id((Long) null)
                .build();

        assertThat(copy).isEqualToIgnoringGivenFields(admin, "id");
        assertThat(copy.getId()).isNotEqualTo(admin.getId());
    }
}
