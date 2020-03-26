package mops.persistence.group;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import mops.util.AggregateBuilder;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Builds file meta data.
 */
@Slf4j
@AggregateBuilder
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@SuppressWarnings({ "PMD.LawOfDemeter", "PMD.TooManyMethods", "PMD.AvoidFieldNameMatchingMethodName",
        "PMD.BeanMembersShouldSerialize" }) // this is a builder
public class GroupBuilder {

    /**
     * Database Id.
     */
    private Long id;
    /**
     * Group Id.
     */
    private UUID groupId;
    /**
     * File name.
     */
    private String name;
    /**
     * File tags.
     */
    private final Set<GroupMember> members = new HashSet<>();
    /**
     * Creation Time.
     */
    private Instant creationTime;

    /**
     * Initialize from existing Group.
     *
     * @param group existing Group
     * @return this
     */
    public GroupBuilder from(@NonNull Group group) {
        this.id = group.getId();
        this.groupId = group.getGroupId();
        this.name = group.getName();
        group.getMembers().forEach(member -> member(member.getName(), member.getRole()));
        this.creationTime = group.getCreationTime();
        return this;
    }

    /**
     * Set id.
     *
     * @param id id
     * @return this
     */
    public GroupBuilder id(Long id) {
        this.id = id;
        return this;
    }

    /**
     * Set id from existing Group.
     *
     * @param group existing Group
     * @return this
     */
    public GroupBuilder id(Group group) {
        this.id = group == null ? null : group.getId();
        return this;
    }

    /**
     * Set group id.
     *
     * @param groupId group id
     * @return this
     */
    public GroupBuilder groupId(@NonNull UUID groupId) {
        this.groupId = groupId;
        return this;
    }

    /**
     * Set name.
     *
     * @param name name
     * @return this
     */
    public GroupBuilder name(@NonNull String name) {
        if (name.isEmpty()) {
            log.error("Failed to set name as it was empty.");
            throw new IllegalArgumentException("name must not be empty!");
        }
        this.name = name;
        return this;
    }

    /**
     * Add member.
     *
     * @param name user name
     * @param role user role in this group
     * @return this
     */
    public GroupBuilder member(@NonNull String name, @NonNull String role) {
        if (name.isEmpty()) {
            log.error("Failed to add member as name was empty.");
            throw new IllegalArgumentException("member name must not be empty!");
        } else if (role.isEmpty()) {
            log.error("Failed to add member as role was empty.");
            throw new IllegalArgumentException("member role must not be empty!");
        } else if (hasMember(name)) {
            log.error("Failed to add member as it already exists.");
            throw new IllegalArgumentException("member must not already exist!");
        }
        this.members.add(new GroupMember(name, role));
        return this;
    }

    /**
     * Tests if the given user is already a member.
     *
     * @param name user name
     * @return true if already a member, false otherwise
     */
    public boolean hasMember(String name) {
        return members.stream().map(GroupMember::getName).anyMatch(name::equals);
    }

    /**
     * Builds the Group.
     *
     * @return composed Group
     * @throws IllegalStateException if Group is not complete
     */
    public Group build() {
        if (groupId == null) {
            log.error("Group is not complete: group id was not set.");
            throw new IllegalStateException("Group incomplete: group id must be set!");
        } else if (name == null) {
            log.error("Group is not complete: name was not set.");
            throw new IllegalStateException("Group incomplete: name must be set!");
        }

        return new Group(
                id,
                groupId,
                name,
                members,
                creationTime == null ? null : Timestamp.from(creationTime),
                null
        );
    }
}
