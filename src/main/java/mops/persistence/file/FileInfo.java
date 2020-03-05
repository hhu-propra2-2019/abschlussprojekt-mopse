package mops.persistence.file;

import lombok.Data;
import mops.persistence.directory.Directory;

@Data
public class FileInfo {

    private Long id;
    private String name;
    private Directory directory;
    private String type;
    private long size;
    private String owner;

    public FileInfo(Long id, String name, Directory directory, String type, long size, String owner) {
        this.id = id;
        this.name = name;
        this.directory = directory;
        this.type = type;
        this.size = size;
        this.owner = owner;
    }

    public FileInfoDO toDO() {
        return new FileInfoDO(name, directory.getId(), type, size, owner);
    }
}
