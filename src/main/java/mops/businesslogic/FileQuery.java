package mops.businesslogic;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import mops.persistence.file.FileInfo;

import java.util.List;
import java.util.function.Function;

@SuppressWarnings("PMD.LawOfDemeter") //these are streams or builders
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class FileQuery {
    /**
     * List of file names to search for.
     */
    private List<String> fileNames;
    /**
     * List of user names of file owners to search for.
     */
    private List<String> owners;
    /**
     * List of file types to search for.
     */
    private List<String> types;

    /**
     * List of file tags to search for.
     */
    private List<String> tags;

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
