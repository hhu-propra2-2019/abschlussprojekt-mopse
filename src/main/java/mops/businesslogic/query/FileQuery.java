package mops.businesslogic.query;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import mops.persistence.file.FileInfo;

import java.util.List;
import java.util.function.Function;

@Value
@AllArgsConstructor(access = AccessLevel.PACKAGE)
// @Value automatically makes all fields `private final` which CheckStyle and PMD don't see
// there are no demeter violations as these are builders and streams
@SuppressWarnings({ "checkstyle:VisibilityModifier", "PMD.DefaultPackage", "PMD.LawOfDemeter" })
public class FileQuery {

    /**
     * List of file names to search for.
     */
    List<String> fileNames;
    /**
     * List of user names of file owners to search for.
     */
    List<String> owners;
    /**
     * List of file types to search for.
     */
    List<String> types;

    /**
     * List of file tags to search for.
     */
    List<String> tags;

    /**
     * Returns a builder for file queries.
     *
     * @return a file query builder
     */
    public static FileQueryBuilder builder() {
        return new FileQueryBuilder();
    }

    /**
     * @param file a file information object
     * @return if the file meta data matches the query request
     */
    public boolean checkMatch(FileInfo file) {
        List<Function<FileInfo, Boolean>> runChecks = List.of(this::checkNames,
                this::checkOwners,
                this::checkTags,
                this::checkTypes);
        return runChecks.stream().allMatch(checkFunction -> checkFunction.apply(file));
    }

    private boolean checkTags(FileInfo file) {
        boolean anyMatch = tags.stream()
                .anyMatch(file::hasTag);
        return tags.isEmpty() || anyMatch;
    }

    private boolean checkTypes(FileInfo file) {
        return types.isEmpty() || types.contains(file.getType());
    }

    private boolean checkNames(FileInfo file) {
        return fileNames.isEmpty() || fileNames.contains(file.getName());
    }

    private boolean checkOwners(FileInfo file) {
        return owners.isEmpty() || owners.contains(file.getOwner());
    }
}
