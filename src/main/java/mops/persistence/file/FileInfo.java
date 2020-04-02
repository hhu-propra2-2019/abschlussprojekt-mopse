package mops.persistence.file;

import lombok.*;
import mops.util.AggregateRoot;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.MappedCollection;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Comparator;
import java.util.Locale;
import java.util.Set;

/**
 * Represents a file.
 */
@Data
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@AggregateRoot
public class FileInfo {

    /**
     * Name Comparator.
     */
    public static final Comparator<FileInfo> NAME_COMPARATOR = Comparator.comparing(FileInfo::getName);

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
    private Timestamp availableFrom;
    /**
     * Creation Time.
     */
    @Setter(AccessLevel.PRIVATE)
    private Timestamp availableTo;
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
        return tags.stream()
                .anyMatch(tag -> tag.getName().toLowerCase(Locale.ROOT)
                        .contains(otherTag.toLowerCase(Locale.ROOT)));
    }

    /**
     * Get the available from time.
     *
     * @return available from time
     */
    public Instant getAvailableFrom() {
        return availableFrom == null ? null : availableFrom.toInstant();
    }

    /**
     * Get the available to time.
     *
     * @return available to time
     */
    public Instant getAvailableTo() {
        return availableTo == null ? null : availableTo.toInstant();
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
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public String getSizeString() {
        final double kibibyte = 1024;
        final double mibibyte = kibibyte * 1024;
        final double gibibyte = mibibyte * 1024;

        double result;
        String suffix;

        if (this.size < kibibyte) {
            result = this.size;
            suffix = "B";
        } else if (this.size < mibibyte) {
            result = this.size / kibibyte;
            suffix = "KiB";
        } else if (this.size < gibibyte) {
            result = this.size / mibibyte;
            suffix = "MiB";
        } else {
            result = this.size / gibibyte;
            suffix = "GiB";
        }
        return String.format("%.2f %s", result, suffix);
    }

    /**
     * Tests whether this file is available at the given time.
     *
     * @param time current time
     * @return if this file is available
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    public boolean isAvailable(Instant time) {
        Instant availableFrom = getAvailableFrom();
        Instant availableTo = getAvailableTo();

        return (availableFrom == null || availableFrom.compareTo(time) <= 0)
                && (availableTo == null || availableTo.compareTo(time) >= 0);
    }
}
