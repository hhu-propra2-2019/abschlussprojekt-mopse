package mops.persistence.file;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import mops.persistence.directory.Directory;
import mops.utils.AggregateBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@AggregateBuilder
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@SuppressWarnings({ "PMD.LawOfDemeter", "PMD.TooManyMethods", "PMD.AvoidFieldNameMatchingMethodName",
        "PMD.BeanMembersShouldSerialize" }) // this is a builder
public class FileInfoBuilder {

    /**
     * Database Id.
     */
    private Long id;
    /**
     * File name.
     */
    private String name;
    /**
     * Id of the Directory this file resides in.
     */
    private long directoryId = -1L;
    /**
     * File type.
     */
    private String type;
    /**
     * Size in bytes.
     */
    private long size = -1L;
    /**
     * Username of the owner.
     */
    private String owner;
    /**
     * File tags.
     */
    private final Set<FileTag> tags = new HashSet<>();
    /**
     * Creation Time.
     */
    private Instant creationTime;

    /**
     * Initialize from existing FileInfo.
     *
     * @param file existing FileInfo
     * @return this
     */
    public FileInfoBuilder from(@NonNull FileInfo file) {
        id = file.getId();
        name = file.getName();
        directoryId = file.getDirectoryId();
        type = file.getType();
        size = file.getSize();
        owner = file.getOwner();
        file.getTags().stream().map(FileTag::getName).forEach(this::tag);
        creationTime = file.getCreationTime();
        return this;
    }

    /**
     * Initialize from existing MultipartFile.
     *
     * @param file existing MultipartFile
     * @return this
     */
    public FileInfoBuilder from(@NonNull MultipartFile file) {
        name = file.getName();
        type = file.getContentType();
        size = file.getSize();
        return this;
    }

    /**
     * Set id.
     *
     * @param id id
     * @return this
     */
    public FileInfoBuilder id(Long id) {
        this.id = id;
        return this;
    }

    /**
     * Set id from existing FileInfo.
     *
     * @param fileInfo existing FileInfo
     * @return this
     */
    public FileInfoBuilder id(FileInfo fileInfo) {
        id = fileInfo == null ? null : fileInfo.getId();
        return this;
    }

    /**
     * Set name.
     *
     * @param name name
     * @return this
     */
    public FileInfoBuilder name(@NonNull String name) {
        if (name.isEmpty()) {
            log.error("Failed to add tag name as it was empty.");
            throw new IllegalArgumentException("name must not be empty!");
        }
        this.name = name;
        return this;
    }

    /**
     * Set directory.
     *
     * @param directoryId is the directoryId
     * @return this
     */
    public FileInfoBuilder directory(long directoryId) {
        this.directoryId = directoryId;
        return this;
    }

    /**
     * Set directory.
     *
     * @param directory directory
     * @return this
     */
    public FileInfoBuilder directory(@NonNull Directory directory) {
        directoryId = directory.getId();
        return this;
    }

    /**
     * Set type.
     *
     * @param type type
     * @return this
     */
    public FileInfoBuilder type(@NonNull String type) {
        if (type.isEmpty()) {
            log.error("Failed to add type as it was empty.");
            throw new IllegalArgumentException("type must not be empty!");
        }
        this.type = type;
        return this;
    }

    /**
     * Set size.
     *
     * @param size size
     * @return this
     */
    public FileInfoBuilder size(long size) {
        this.size = size;
        return this;
    }

    /**
     * Set owner.
     *
     * @param owner owner
     * @return this
     */
    public FileInfoBuilder owner(@NonNull String owner) {
        if (owner.isEmpty()) {
            log.error("Failed to add owner as it was empty.");
            throw new IllegalArgumentException("owner must not be empty!");
        }
        this.owner = owner;
        return this;
    }

    /**
     * Add tag.
     *
     * @param tag tag
     * @return this
     */
    public FileInfoBuilder tag(@NonNull String tag) {
        if (tag.isEmpty()) {
            log.error("Failed to add tag as it was empty.");
            throw new IllegalArgumentException("tag must not be empty!");
        }
        tags.add(new FileTag(tag));
        return this;
    }

    /**
     * Add tags.
     *
     * @param tags tags
     * @return this
     */
    public FileInfoBuilder tags(@NonNull String... tags) {
        Arrays.stream(tags).forEach(this::tag);
        return this;
    }

    /**
     * Add tags.
     *
     * @param tags tags
     * @return this
     */
    public FileInfoBuilder tags(@NonNull Iterable<String> tags) {
        tags.forEach(this::tag);
        return this;
    }

    /**
     * Builds the FileInfo.
     *
     * @return composed FileInfo
     * @throws IllegalStateException if FileInfo is not complete
     */
    @SuppressWarnings("PMD.CyclomaticComplexity") //if-else chain necessary for fine grained exception messages
    public FileInfo build() {
        if (name == null) {
            log.error("Directory is not complete: name was not set.");
            throw new IllegalStateException("FileInfo incomplete: name must be set!");
        } else if (directoryId == -1L) {
            log.error("Directory is not complete: directory id not set.");
            throw new IllegalStateException("FileInfo incomplete: directoryId must be set!");
        } else if (type == null) {
            log.error("Directory is not complete: type was not set.");
            throw new IllegalStateException("FileInfo incomplete: type must be set!");
        } else if (size == -1L) {
            log.error("Directory is not complete: size was not set.");
            throw new IllegalStateException("FileInfo incomplete: size must be set!");
        } else if (owner == null) {
            log.error("Directory is not complete: owner was not set.");
            throw new IllegalStateException("FileInfo incomplete: owner must be set!");
        }

        return new FileInfo(
                id,
                name,
                directoryId,
                type,
                size,
                owner,
                tags,
                creationTime == null ? null : Timestamp.from(creationTime),
                null
        );
    }
}
