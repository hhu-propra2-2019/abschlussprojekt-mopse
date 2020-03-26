package mops.persistence.group;

import lombok.*;
import mops.util.AggregateRoot;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a group.
 */
@Data
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@AggregateRoot
@Table("group_table")
public class Group {

    /**
     * Database id.
     */
    @Id
    private UUID id;
    /**
     * Group name.
     */
    @NonNull
    private String name;
    /**
     * File tags.
     */
    @NonNull
    @MappedCollection(idColumn = "group_id")
    private Set<GroupMember> members;
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
     * Gives you GroupBuilder.
     *
     * @return GroupBuilder
     */
    public static GroupBuilder builder() {
        return new GroupBuilder();
    }

    /**
     * Get the role of a group member.
     *
     * @param name member name
     * @return role in group
     */
    @SuppressWarnings("PMD.LawOfDemeter") // stream
    public String getMemberRole(String name) {
        return members.stream()
                .filter(member -> member.getName().equals(name))
                .findFirst()
                .map(GroupMember::getRole)
                .orElse("intruder");
    }
}
