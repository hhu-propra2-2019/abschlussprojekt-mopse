package mops.persistence.file;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("file_info")
public class FileInfoDO {

    @Id
    private Long id;
    private String name;
    private long directory;
    private String type;
    private long size;
    private String owner;

    public FileInfoDO(String name, long directory, String type, long size, String owner) {
        this.name = name;
        this.directory = directory;
        this.type = type;
        this.size = size;
        this.owner = owner;
    }
}
