package mops.persistence.directory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Directory {

    @Id
    private Long id;
    private String name;
    private Long parentId;
    private long groupOwner;
    private long permissionsId;

    public Directory(String name, Long parentId, long groupOwner, long permissionsId) {
        this.name = name;
        this.parentId = parentId;
        this.groupOwner = groupOwner;
        this.permissionsId = permissionsId;
    }
}
