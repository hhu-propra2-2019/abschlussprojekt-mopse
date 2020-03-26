package mops.businesslogic.group;

import lombok.extern.slf4j.Slf4j;
import mops.businesslogic.exception.DatabaseException;
import mops.businesslogic.security.Account;
import mops.exception.MopsException;
import mops.persistence.group.Group;
import mops.persistence.group.GroupBuilder;
import mops.persistence.permission.DirectoryPermissions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * {@inheritDoc}
 * This is used during development and will return dummies.
 */
@Slf4j
@Service
@Profile({ "dev", "test" })
public class GroupServiceDevImpl implements GroupService {

    /**
     * Group id.
     */
    private static final Set<Long> VALID_GROUP_IDS = Set.of(100L);

    /**
     * Represents the role of an admin.
     */
    @Value("${material1.mops.configuration.role.admin}")
    @SuppressWarnings({ "PMD.ImmutableField", "PMD.BeanMembersShouldSerialize" })
    private String adminRole;
    /**
     * Represents the role of a viewer.
     */
    @Value("${material1.mops.configuration.role.viewer}")
    @SuppressWarnings({ "PMD.ImmutableField", "PMD.BeanMembersShouldSerialize" })
    private String viewerRole;

    /**
     * Group cache.
     */
    @SuppressWarnings("PMD.BeanMembersShouldSerialize")
    private final Map<Long, Group> cachedGroups = VALID_GROUP_IDS.stream()
            .map(id -> Group.builder()
                    .id(id)
                    .groupId(new UUID(0, id))
                    .name("Einzigen #" + id)
                    .build()
            )
            .collect(Collectors.toMap(Group::getId, Function.identity()));

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean doesGroupExist(long groupId) throws MopsException {
        return VALID_GROUP_IDS.contains(groupId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.OnlyOneReturn")
    public Set<String> getRoles(long groupId) throws MopsException {
        return Set.of(adminRole, viewerRole);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.LawOfDemeter") // stream
    public List<Group> getAllGroups() throws MopsException {
        return List.copyOf(cachedGroups.values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "PMD.LawOfDemeter", "PMD.UseConcurrentHashMap" }) // builder
    public List<Group> getUserGroups(Account account) throws MopsException {
        Map<Long, Group> newCache = new HashMap<>();
        for (Map.Entry<Long, Group> entry : cachedGroups.entrySet()) {
            long id = entry.getKey();
            Group group = entry.getValue();

            GroupBuilder newGroupBuilder = Group.builder().from(group);
            if (!newGroupBuilder.hasMember(account.getName())) {
                newGroupBuilder.member(account.getName(), getUserRole(account, group));
            }
            newCache.put(id, newGroupBuilder.build());
        }
        cachedGroups.putAll(newCache);

        return getAllGroups(); // every user is in every group
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "PMD.LawOfDemeter" }) //Streams
    public DirectoryPermissions getDefaultPermissions(long groupId) {
        return DirectoryPermissions.builder()
                .entry(adminRole, true, true, true)
                .entry(viewerRole, true, false, false)
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Group getGroup(long groupId) throws MopsException {
        Group group = cachedGroups.get(groupId);
        if (group == null) {
            throw new DatabaseException("Die Gruppe konnte nicht gefunden werden");
        }

        return group;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Group> findGroupByGroupId(UUID groupId) throws MopsException {
        return cachedGroups.values().stream()
                .filter(g -> g.getGroupId().equals(groupId))
                .findFirst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Group> saveAllGroups(Collection<Group> groups) throws MopsException {
        throw new MopsException("nicht unterstützt", new UnsupportedOperationException("not supported"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAllGroups(Collection<Long> groupIds) throws MopsException {
        throw new MopsException("nicht unterstützt", new UnsupportedOperationException("nyi"));
    }

    @SuppressWarnings({ "PMD.LawOfDemeter", "PMD.OnlyOneReturn" }) // these are streams
    private String getUserRole(Account account, Group group) {
        log.debug("Fetching group role for user '{}' with keycloak roles '{}' in group '{}'.",
                account.getName(), account.getRoles(), group.getName());

        if (account.getRoles().stream().anyMatch(role -> role.contains("admin"))) {
            log.debug("Found 'admin' keycloak role, returning group role 'admin'.");
            return adminRole;
        } else if (account.getRoles().stream()
                .anyMatch(role -> role.contains("orga") || role.contains("korrektor"))) {
            log.debug("Found 'orga'/'korrektorin' keycloak role, returning group role 'admin'.");
            return adminRole;
        } else {
            log.debug("Returning group role 'viewer'.");
            return viewerRole;
        }
    }
}
