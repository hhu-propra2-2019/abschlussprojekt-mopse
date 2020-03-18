package mops.businesslogic;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@SuppressFBWarnings(value = "EI_EXPOSE_REP",
        justification = "This class is just a wrapper, so it's ok to expose the getters")
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
