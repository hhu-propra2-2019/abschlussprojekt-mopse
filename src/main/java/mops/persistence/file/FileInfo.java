package mops.persistence.file;

import lombok.*;
import mops.utils.AggregateRoot;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.relational.core.mapping.MappedCollection;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Set;

/**
 * Represents a file.
 */
@Data
@AllArgsConstructor(access = AccessLevel.PACKAGE, onConstructor_ = @PersistenceConstructor)
@AggregateRoot
public class FileInfo {

    /**
     * Database Id.
     */
    @Id
    @Setter(AccessLevel.PRIVATE)
    private Long id;
    /**
     * File name.
     */
    @NonNull
    private String name;
    /**
     * Id of the Directory this file resides in.
     */
    private long directoryId;
    /**
     * File type.
     */
    @NonNull
    private String type;
    /**
     * Size in bytes.
     */
    private long size;
    /**
     * Username of the owner.
     */
    @NonNull
    private String owner;
    /**
     * Creation Time.
     */
    @CreatedDate
    @Setter(AccessLevel.PRIVATE)
    private Timestamp creationTime;
    /**
     * Last Modified Time.
     */
    @LastModifiedDate
    @Setter(AccessLevel.PRIVATE)
    private Timestamp lastModifiedTime;
    /**
     * File tags.
     */
    @NonNull
    @MappedCollection(idColumn = "file_id")
    private final Set<FileTag> tags;

    /**
     * Create a new File.
     *
     * @param name        file name
     * @param directoryId id of directory this file is in
     * @param type        file type
     * @param size        file size
     * @param owner       file owner
     * @param tags        file tags
     */
    public FileInfo(@NonNull String name, long directoryId, @NonNull String type, long size, @NonNull String owner,
                    @NonNull Set<FileTag> tags) {
        this(null, name, directoryId, type, size, owner, null, null, tags);
    }

    /**
     * Get the creation time.
     *
     * @return creation time
     */
    public Instant getCreationTime() {
        return creationTime.toInstant();
    }

    /**
     * Get the last modified time.
     *
     * @return last modified time
     */
    public Instant getLastModifiedTime() {
        return lastModifiedTime.toInstant();
    }
}
