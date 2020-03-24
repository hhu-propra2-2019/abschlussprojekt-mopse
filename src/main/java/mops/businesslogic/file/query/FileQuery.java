package mops.businesslogic.file.query;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import mops.persistence.file.FileInfo;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Wrapper for file search requests.
 */
@Value
@AllArgsConstructor(access = AccessLevel.PACKAGE)
// @Value automatically makes all fields `private final` which CheckStyle and PMD don't see
// there are no demeter violations as these are builders and streams
@SuppressWarnings({ "checkstyle:VisibilityModifier", "PMD.DefaultPackage", "PMD.LawOfDemeter" })
public class FileQuery {

    /**
     * List of file names to search for.
     */
    Set<String> names;
    /**
     * List of user names of file owners to search for.
     */
    Set<String> owners;
    /**
     * List of file types to search for.
     */
    Set<String> types;
    /**
     * List of file tags to search for.
     */
    Set<String> tags;

    /**
     * Returns a builder for file queries.
     *
     * @return a file query builder
     */
    public static FileQueryBuilder builder() {
        return new FileQueryBuilder();
    }

    /**
     * Checks if file query matches with file meta data.
     *
     * @param file a file information object
     * @return if the file meta data matches the query request
     */
    public boolean checkMatch(FileInfo file) {
        List<Predicate<FileInfo>> runChecks = List.of(this::checkNames,
                this::checkOwners,
                this::checkTags,
                this::checkTypes);
        return runChecks.stream().allMatch(check -> check.test(file));
    }

    private boolean checkTags(FileInfo file) {
        boolean anyMatch = tags.stream().anyMatch(file::hasTag);
        return tags.isEmpty() || anyMatch;
    }

    @SuppressWarnings("PMD.OnlyOneReturn")
    private boolean checkTypes(FileInfo file) {
        return types.stream()
                .anyMatch(type -> file.getType()
                        .toLowerCase(Locale.ROOT)
                        .contains(type.toLowerCase(Locale.ROOT))) || types.isEmpty();
    }

    private boolean checkNames(FileInfo file) {
        return names.stream()
                .anyMatch(name -> file.getName()
                        .toLowerCase(Locale.ROOT)
                        .contains(name.toLowerCase(Locale.ROOT))) || names.isEmpty();
    }

    @SuppressWarnings("PMD.OnlyOneReturn")
    private boolean checkOwners(FileInfo file) {
        return owners.stream()
                .anyMatch(owner -> file.getOwner()
                        .toLowerCase(Locale.ROOT)
                        .contains(owner.toLowerCase(Locale.ROOT))) || owners.isEmpty();
    }
}
