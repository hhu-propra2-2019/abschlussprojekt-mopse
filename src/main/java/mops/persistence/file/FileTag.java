package mops.persistence.file;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileTag {

    @Id
    private Long id;
    private String name;

    public FileTag(String name) {
        this.name = name;
    }
}
