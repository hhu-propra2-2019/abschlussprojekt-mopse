package mops.persistence.permission;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DirectoryPermissionsBuilderTest {
    @Test
    public void copyTest() {
        DirectoryPermissions parentPermissions = DirectoryPermissions.builder()
                .id(1L)
                .entry("admin", true, true, true)
                .build();

        DirectoryPermissions copy = DirectoryPermissions.builder()
                .copy(parentPermissions)
                .build();

        assertThat(copy).isEqualToIgnoringGivenFields(parentPermissions, "id")
                .hasFieldOrPropertyWithValue("id", null);
    }
}
