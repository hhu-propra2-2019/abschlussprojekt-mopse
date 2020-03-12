package mops.persistence.file;

import mops.persistence.directory.Directory;
import mops.utils.AggregateBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@AggregateBuilder
@SuppressWarnings({ "PMD.LawOfDemeter", "PMD.TooManyMethods", "PMD.AvoidFieldNameMatchingMethodName",
        "PMD.BeanMembersShouldSerialize" })
public class FileInfoBuilder {

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
     * Initialize from existing FileInfo.
     *
     * @param file existing FileInfo
     * @return this
     */
    public FileInfoBuilder from(FileInfo file) {
        this.name = file.getName();
        this.directoryId = file.getDirectoryId();
        this.type = file.getType();
        this.size = file.getSize();
        this.owner = file.getOwner();
        file.getTags().stream().map(FileTag::getName).forEach(this::tag);
        return this;
    }

    /**
     * Initialize from existing MultipartFile.
     *
     * @param file existing MultipartFile
     * @return this
     */
    public FileInfoBuilder from(MultipartFile file) {
        this.name = file.getName();
        this.type = file.getContentType();
        this.size = file.getSize();
        return this;
    }

    /**
     * Set name.
     *
     * @param name name
     * @return this
     */
    public FileInfoBuilder name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Set directory.
     *
     * @param directoryId is the directoryId
     * @return this
     */
    public FileInfoBuilder directoryId(long directoryId) {
        this.directoryId = directoryId;
        return this;
    }

    /**
     * Set directory.
     *
     * @param directory directory
     * @return this
     */
    public FileInfoBuilder directory(Directory directory) {
        this.directoryId = directory.getId();
        return this;
    }

    /**
     * Set type.
     *
     * @param type type
     * @return this
     */
    public FileInfoBuilder type(String type) {
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
    public FileInfoBuilder owner(String owner) {
        this.owner = owner;
        return this;
    }

    /**
     * Add tag.
     *
     * @param tag tag
     * @return this
     */
    public FileInfoBuilder tag(String tag) {
        this.tags.add(new FileTag(tag));
        return this;
    }

    /**
     * Add tags.
     *
     * @param tags tags
     * @return this
     */
    public FileInfoBuilder tags(String... tags) {
        Arrays.stream(tags).forEach(this::tag);
        return this;
    }

    /**
     * Add tags.
     *
     * @param tags tags
     * @return this
     */
    public FileInfoBuilder tags(Iterable<String> tags) {
        tags.forEach(this::tag);
        return this;
    }

    /**
     * Builds the FileInfo.
     *
     * @return composed FileInfo
     * @throws IllegalStateException if FileInfoBuilder is not complete
     */
    public FileInfo build() {
        if (name == null || directoryId == -1L || type == null || size == -1L || owner == null) {
            throw new IllegalStateException("FileInfoBuilder is not complete!");
        }
        return new FileInfo(null, name, directoryId, type, size, owner, tags, null, null);
    }
}
