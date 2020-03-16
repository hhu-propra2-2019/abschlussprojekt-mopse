package mops.businesslogic.query;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@SuppressWarnings({ "PMD.LawOfDemeter", "PMD.TooManyMethods", "PMD.AvoidFieldNameMatchingMethodName",
        "PMD.BeanMembersShouldSerialize" }) // this is a builder
public class FileQueryBuilder {

    /**
     * Keys for owners.
     */
    private static final Set<String> OWNERS = Set.of("owner", "owners");
    /**
     * Keys for file names.
     */
    private static final Set<String> FILE_NAMES = Set.of("filename", "filenames", "name", "names");
    /**
     * Keys for types.
     */
    private static final Set<String> TYPES = Set.of("type", "types");
    /**
     * Keys for tags.
     */
    private static final Set<String> TAGS = Set.of("tag", "tags");
    /**
     * Separator for keys. (key_:_value)
     */
    private static final char KEY_SEPARATOR = ':';
    /**
     * Marker for strings. (key:_"_value with spaces_"_)
     */
    private static final char STRING_MARKER = '"';

    /**
     * List of owner to search for.
     */
    private final List<String> owners = new ArrayList<>();
    /**
     * Names of files to search for.
     */
    private final List<String> fileNames = new ArrayList<>();
    /**
     * File types to search for.
     */
    private final List<String> types = new ArrayList<>();
    /**
     * File tags to search for.
     */
    private final List<String> tags = new ArrayList<>();

    /**
     * Create FileQuery from search string.
     *
     * @param search search string
     * @return this
     */
    @SuppressWarnings("PMD.CyclomaticComplexity")
    public FileQueryBuilder from(@NonNull String search) {
        List<String> tokens = tokenize(search);

        int length = tokens.size();
        int index = 0;

        while (index < length) {
            String oldToken = tokens.get(index);
            String token = oldToken.toLowerCase(Locale.ROOT);

            if (OWNERS.contains(token) && index + 1 < length) {
                owner(tokens.get(++index));
            } else if (FILE_NAMES.contains(token) && index + 1 < length) {
                fileName(tokens.get(++index));
            } else if (TYPES.contains(token) && index + 1 < length) {
                type(tokens.get(++index));
            } else if (TAGS.contains(token) && index + 1 < length) {
                tag(tokens.get(++index));
            } else { // values without keys are file names
                fileName(oldToken);
            }

            index++;
        }

        return this;
    }

    // pmd doesn't like tokenizers
    @SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.DataflowAnomalyAnalysis" })
    private List<String> tokenize(String search) {
        int length = search.length();

        boolean isToken = false;
        boolean isString = false;
        StringBuilder tokenBuilder = new StringBuilder();
        List<String> tokens = new ArrayList<>();

        for (int index = 0; index < length; index++) {
            char current = search.charAt(index);

            if (!isToken && Character.isWhitespace(current)) {
                continue;
            }

            if (isToken && !isString && (Character.isWhitespace(current) || current == KEY_SEPARATOR)) {
                isToken = false;
                String token = tokenBuilder.toString();
                tokenBuilder.setLength(0);
                tokens.add(token);
                continue;
            }

            if (current == STRING_MARKER) {
                if (!isToken) {
                    isToken = true;
                    isString = true;
                    continue;
                }
                if (isString) {
                    isToken = false;
                    isString = false;
                    String token = tokenBuilder.toString();
                    tokenBuilder.setLength(0);
                    tokens.add(token);
                    continue;
                }
            }

            if (!isToken) {
                isToken = true;
            }

            tokenBuilder.append(current);
        }

        if (isToken) {
            String token = tokenBuilder.toString();
            tokenBuilder.setLength(0);
            tokens.add(token);
        }

        return List.copyOf(tokens);
    }

    /**
     * @param owners list of owner to search for
     * @return this
     */
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
            throw new IllegalArgumentException("type must not be empty!");
        }
        types.add(type);
        return this;
    }

    /**
     * @param tags what the file should be tagged with
     * @return this
     */
    public FileQueryBuilder tags(@NonNull Iterable<String> tags) {
        tags.forEach(this::tag);
        return this;
    }

    private FileQueryBuilder tag(@NonNull String tag) {
        if (tag.isEmpty()) {
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
                List.copyOf(fileNames),
                List.copyOf(owners),
                List.copyOf(types),
                List.copyOf(tags)
        );
    }
}
