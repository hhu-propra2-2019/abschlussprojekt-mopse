package mops.persistence.directory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DirectoryPermissions {

    @Id
    private Long id;
    @MappedCollection(idColumn = "permission")
    private Set<DirectoryPermissionEntry> entries;

    public DirectoryPermissions(Set<DirectoryPermissionEntry> entries) {
        this.entries = entries;
    }
}
