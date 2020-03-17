package mops.businesslogic.query;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.*;

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
    private static final Set<String> NAMES = Set.of("name", "names");
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
     * Marker for the escape character. (key:"valueA_\_valueB"")
     */
    private static final char ESCAPE_CHAR = '\\';

    /**
     * List of owner to search for.
     */
    private final Set<String> owners = new HashSet<>();
    /**
     * Names of files to search for.
     */
    private final Set<String> names = new HashSet<>();
    /**
     * File types to search for.
     */
    private final Set<String> types = new HashSet<>();
    /**
     * File tags to search for.
     */
    private final Set<String> tags = new HashSet<>();

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
            } else if (NAMES.contains(token) && index + 1 < length) {
                name(tokens.get(++index));
            } else if (TYPES.contains(token) && index + 1 < length) {
                type(tokens.get(++index));
            } else if (TAGS.contains(token) && index + 1 < length) {
                tag(tokens.get(++index));
            } else { // values without keys are file names
                name(oldToken);
            }

            index++;
        }

        return this;
    }

    // pmd doesn't like tokenizers
    @SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.NPathComplexity", "PMD.DataflowAnomalyAnalysis" })
    private List<String> tokenize(String search) {
        int length = search.length();

        boolean isToken = false;
        boolean isString = false;
        boolean isEscape = false;
        StringBuilder tokenBuilder = new StringBuilder();
        List<String> tokens = new ArrayList<>();

        for (int index = 0; index < length; index++) {
            char current = search.charAt(index);

            if (!isToken && Character.isWhitespace(current)) {
                continue;
            }

            if (!isToken && current == KEY_SEPARATOR) {
                continue;
            }

            if (isToken && !isString && (Character.isWhitespace(current) || current == KEY_SEPARATOR)) {
                isToken = false;
                String token = tokenBuilder.toString();
                tokenBuilder.setLength(0);
                tokens.add(token);
                continue;
            }

            if (isString && current == ESCAPE_CHAR && !isEscape) {
                isEscape = true;
                continue;
            }

            if (current == STRING_MARKER && !isEscape) {
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

            if (isEscape) {
                isEscape = false;
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
     * Add all owners.
     *
     * @param owners list of owner to search for
     * @return this
     */
    public FileQueryBuilder owners(@NonNull Iterable<String> owners) {
        owners.forEach(this::owner);
        return this;
    }

    /**
     * Add an owner.
     *
     * @param owner one Owner
     * @return this
     */
    public FileQueryBuilder owner(@NonNull String owner) {
        if (owner.isEmpty()) {
            throw new IllegalArgumentException("owner must not be empty!");
        }
        owners.add(owner.toLowerCase(Locale.ROOT));
        return this;

    }

    /**
     * Add all file names.
     *
     * @param names names of files to search for
     * @return this
     */
    public FileQueryBuilder names(@NonNull Iterable<String> names) {
        names.forEach(this::name);
        return this;
    }

    /**
     * Add a file name.
     *
     * @param name new file name to search for
     * @return this
     */
    public FileQueryBuilder name(@NonNull String name) {
        if (name.isEmpty()) {
            throw new IllegalArgumentException("name must not be empty!");
        }
        names.add(name.toLowerCase(Locale.ROOT));
        return this;
    }

    /**
     * Add all file types.
     *
     * @param types file types to search for
     * @return this
     */
    public FileQueryBuilder types(@NonNull Iterable<String> types) {
        types.forEach(this::type);
        return this;
    }

    /**
     * Add a file type.
     *
     * @param type new type to search for
     * @return this
     */
    public FileQueryBuilder type(@NonNull String type) {
        if (type.isEmpty()) {
            throw new IllegalArgumentException("type must not be empty!");
        }
        types.add(type.toLowerCase(Locale.ROOT));
        return this;
    }

    /**
     * Add all tags.
     *
     * @param tags what the file should be tagged with
     * @return this
     */
    public FileQueryBuilder tags(@NonNull Iterable<String> tags) {
        tags.forEach(this::tag);
        return this;
    }

    /**
     * Add a tag.
     *
     * @param tag what the file should be tagged with
     * @return this
     */
    public FileQueryBuilder tag(@NonNull String tag) {
        if (tag.isEmpty()) {
            throw new IllegalArgumentException("tag must not be empty!");
        }
        tags.add(tag.toLowerCase(Locale.ROOT));
        return this;
    }

    /**
     * Builds the object from it's information.
     *
     * @return file query object
     */
    public FileQuery build() {
        return new FileQuery(
                Set.copyOf(names),
                Set.copyOf(owners),
                Set.copyOf(types),
                Set.copyOf(tags)
        );
    }
}
