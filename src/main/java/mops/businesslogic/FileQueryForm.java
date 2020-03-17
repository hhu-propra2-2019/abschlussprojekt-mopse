package mops.businesslogic;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@SuppressFBWarnings("EI_EXPOSE_REP")
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
