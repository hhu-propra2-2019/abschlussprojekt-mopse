package mops.persistence.directory.permission;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DirectoryPermissionEntry {

    @Id
    private Long id;
    private String role;
    private boolean canRead;
    private boolean canWrite;
    private boolean canDelete;


    public DirectoryPermissionEntry(String role, boolean canRead, boolean canWrite, boolean canDelete) {
        this.role = role;
        this.canRead = canRead;
        this.canWrite = canWrite;
        this.canDelete = canDelete;
    }
}
