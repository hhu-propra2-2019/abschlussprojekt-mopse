package mops.persistence.file;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Short meta information strings.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileTag {

    /**
     * Tag name.
     */
    @NonNull
    private String name;

}
