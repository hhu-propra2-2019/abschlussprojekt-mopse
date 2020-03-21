package mops.businesslogic.file.query;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * Builds a file query from string.
 */
public class FileQueryConverter implements Converter<String, FileQuery> {

    /**
     * Singleton instance.
     */
    public static final FileQueryConverter INSTANCE = new FileQueryConverter();

    private FileQueryConverter() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    @SuppressWarnings("PMD.LawOfDemeter") // builder usage
    public FileQuery convert(@NonNull String search) {
        return FileQuery.builder()
                .from(search)
                .build();
    }
}
