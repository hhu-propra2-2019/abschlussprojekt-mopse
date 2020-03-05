package mops.persistence.directory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Directory {

    @Id
    private Long id;
    private String name;
    private long parent;
    private long groupOwner;
    @Column("directory")
    private DirectoryPermissions permissions;

    public Directory(String name, long parent, long groupOwner, DirectoryPermissions permissions) {
        this.name = name;
        this.parent = parent;
        this.groupOwner = groupOwner;
        this.permissions = permissions;
    }
}
