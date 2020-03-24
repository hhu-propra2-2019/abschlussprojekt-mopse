package mops.persistence.file;

import lombok.*;
import mops.util.AggregateRoot;
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
     * File tags.
     */
    @NonNull
    @MappedCollection(idColumn = "file_id")
    private Set<FileTag> tags;
    /**
     * Creation Time.
     */
    @Setter(AccessLevel.PRIVATE)
    @EqualsAndHashCode.Exclude
    @CreatedDate
    private Timestamp creationTime;
    /**
     * Last Modified Time.
     */
    @Setter(AccessLevel.PRIVATE)
    @EqualsAndHashCode.Exclude
    @LastModifiedDate
    private Timestamp lastModifiedTime;

    /**
     * Gives you FileInfoBuilder.
     *
     * @return FileInfoBuilder
     */
    public static FileInfoBuilder builder() {
        return new FileInfoBuilder();
    }

    /**
     * Checks if the file is tagged with a specific tag.
     *
     * @param otherTag tag to check for
     * @return boolean
     */
    @SuppressWarnings("PMD.LawOfDemeter") //this is a stream
    public boolean hasTag(String otherTag) {
        return tags.stream().anyMatch(tag -> otherTag.equalsIgnoreCase(tag.getName()));
    }

    /**
     * Get the creation time.
     *
     * @return creation time
     */
    public Instant getCreationTime() {
        return creationTime == null ? Instant.EPOCH : creationTime.toInstant();
    }

    /**
     * Get the last modified time.
     *
     * @return last modified time
     */
    public Instant getLastModifiedTime() {
        return lastModifiedTime == null ? Instant.EPOCH : lastModifiedTime.toInstant();
    }

    /**
     * Converts Byte to a String.
     *
     * @return Filesize with prefix.
     */
    public String getSizeString() {
        final double kilobyte = 1024;
        final double megabyte = kilobyte * 1024;
        final double gigabyte = megabyte * 1024;

        double result;
        String suffix;

        if (this.size < kilobyte) {
            result = this.size;
            suffix = " B";
        } else if (this.size > kilobyte && this.size < megabyte) {
            result = this.size / kilobyte;
            suffix = " Kb";
        } else if (this.size > megabyte && this.size < gigabyte) {
            result = this.size / megabyte;
            suffix = " Mb";
        } else {
            result = this.size / gigabyte;
            suffix = " Gb";
        }
        return String.format("%.2f" + suffix, result);
    }
}
