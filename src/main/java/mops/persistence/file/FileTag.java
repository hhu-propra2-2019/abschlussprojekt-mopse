package mops.persistence.file;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;

/**
 * Short meta information strings.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class FileTag {

    /**
     * Database Id.
     */
    @Id
    private Long id;
    /**
     * Tag name.
     */
    @NonNull
    private String name;

    /**
     * Create a new Tag.
     *
     * @param name tag name
     */
    FileTag(String name) {
        this.name = name;
    }
}
