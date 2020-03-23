package mops.presentation.form;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Data;
import lombok.NoArgsConstructor;
import mops.businesslogic.file.query.FileQuery;
import mops.businesslogic.file.query.FileQueryBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        FileQueryBuilder fileQueryBuilder = FileQuery.builder();
        if (names != null) {
            fileQueryBuilder.names(removeEmptyStrings(names));
        }
        if (owners != null) {
            fileQueryBuilder.owners(removeEmptyStrings(owners));
        }
        if (tags != null) {
            fileQueryBuilder.tags(removeEmptyStrings(tags));
        }
        if (types != null) {
            fileQueryBuilder.types(removeEmptyStrings(types));
        }
        return fileQueryBuilder.build();
    }

    @SuppressWarnings("PMD.LawOfDemeter")
    private List<String> removeEmptyStrings(String... array) {
        return Arrays.stream(array)
                .filter(string ->  !string.isEmpty())
                .collect(Collectors.toList());
    }
}
