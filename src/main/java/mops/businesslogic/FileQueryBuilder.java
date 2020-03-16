package mops.businesslogic;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@SuppressWarnings({ "PMD.LawOfDemeter", "PMD.TooManyMethods", "PMD.AvoidFieldNameMatchingMethodName",
        "PMD.BeanMembersShouldSerialize" }) // this is a builder
public class FileQueryBuilder {

    /**
     * List of owner to search for.
     */
    private List<String> owners = new ArrayList<>();
    /**
     * Names of files to search for.
     */
    private List<String> fileNames = new ArrayList<>();
    /**
     * File types to search for.
     */
    private List<String> types = new ArrayList<>();
    /**
     * File tags to search for.
     */
    private List<String> tags = new ArrayList<>();

    /**
     * @param owners list of owner to search for
     * @return this
     */
    @SuppressWarnings("PMD.LawOfDemeter") //this is a stream
    public FileQueryBuilder owners(@NonNull Iterable<String> owners) {
        owners.forEach(this::owner);
        return this;
    }

    /**
     * Adds one owner to search for.
     *
     * @param owner one Owner
     * @return this
     */
    public FileQueryBuilder owner(@NonNull String owner) {
        if (owner.isEmpty()) {
            log.error("Search parameter owner was an empty string.");
            throw new IllegalArgumentException("owner must not be empty!");
        }
        owners.add(owner);
        return this;

    }

    /**
     * @param fileNames names of files to search for
     * @return this
     */
    public FileQueryBuilder names(@NonNull Iterable<String> fileNames) {
        fileNames.forEach(this::fileName);
        return this;
    }

    /**
     * @param fileName new file name to search for
     * @return this
     */
    public FileQueryBuilder fileName(@NonNull String fileName) {
        if (fileName.isEmpty()) {
            log.error("Search parameter file name was an empty string.");
            throw new IllegalArgumentException("fileName must not be empty!");
        }
        fileNames.add(fileName);
        return this;
    }

    /**
     * @param types file types to search for
     * @return this
     */
    public FileQueryBuilder types(@NonNull Iterable<String> types) {
        types.forEach(this::type);
        return this;
    }

    /**
     * @param type new type to search for
     * @return this
     */
    private FileQueryBuilder type(@NonNull String type) {
        if (type.isEmpty()) {
            log.error("Search parameter type was an empty string.");
            throw new IllegalArgumentException("type must not be empty!");
        }
        types.add(type);
        return this;
    }

    /**
     * @param tags what the file should be tagged with
     * @return this
     */
    @SuppressWarnings("PMD.LawOfDemeter") //this is a stream
    public FileQueryBuilder tags(@NonNull Iterable<String> tags) {
        tags.forEach(this::tag);
        return this;
    }

    private FileQueryBuilder tag(@NonNull String tag) {
        if (tag.isEmpty()) {
            log.error("Search parameter tag was an empty string.");
            throw new IllegalArgumentException("tag must not be empty!");
        }
        tags.add(tag);
        return this;
    }

    /**
     * Builds the object from it's information.
     *
     * @return file query object
     */
    public FileQuery build() {
        return new FileQuery(
                fileNames,
                owners,
                types,
                tags
        );
    }
}
