package mops.persistence.file;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileInfo {

    @Id
    private Long id;
    private String name;
    private long directoryId;
    private String type;
    private long size;
    private String owner;

    public FileInfo(String name, long directoryId, String type, long size, String owner) {
        this.name = name;
        this.directoryId = directoryId;
        this.type = type;
        this.size = size;
        this.owner = owner;
    }
}
