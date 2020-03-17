package mops.businesslogic;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class FileQueryForm {
    /**
     * List of file names to search for.
     */
    private String[] fileNames;
    /**
     * List of user names of file owners to search for.
     */
    private String[] owners;
    /**
     * List of file types to search for.
     */
    private String[] types;

    /**
     * List of file tags to search for.
     */
    private String[] tags;
}
