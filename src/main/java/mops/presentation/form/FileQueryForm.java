package mops.presentation.form;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Data;
import lombok.NoArgsConstructor;
import mops.businesslogic.file.query.FileQuery;

import java.util.Arrays;

/**
 * Wrapper for FileQuery for thymeleaf.
 */
@NoArgsConstructor
@Data
@SuppressFBWarnings(value = "EI_EXPOSE_REP",
        justification = "This class is just a wrapper, so it's ok to expose the getters")
public class FileQueryForm {

    /**
     * List of file names to search for.
     */
    private String[] names;
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

    /**
     * Create FileQuery from FileQueryForm.
     *
     * @return the FileQuery
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    public FileQuery toQuery() {
        return FileQuery.builder()
                .names(Arrays.asList(names))
                .owners(Arrays.asList(owners))
                .types(Arrays.asList(types))
                .tags(Arrays.asList(tags))
                .build();
    }
}
