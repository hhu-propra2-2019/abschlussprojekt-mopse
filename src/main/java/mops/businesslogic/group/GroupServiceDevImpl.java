package mops.businesslogic.group;

import lombok.extern.slf4j.Slf4j;
import mops.businesslogic.exception.DatabaseException;
import mops.businesslogic.security.Account;
import mops.exception.MopsException;
import mops.persistence.group.Group;
import mops.persistence.group.GroupBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private String adminRole = "admin";
    /**
     * Represents the role of a viewer.
     */
    @Value("${material1.mops.configuration.role.viewer}")
    private String viewerRole = "viewer";

    /**
     * Group cache.
     */
    private final Map<Long, Group> cachedGroups = VALID_GROUP_IDS.stream()
            .map(id -> Group.builder()
                    .id(id)
                    .name("Einzigen #" + id)
                    .build()
            )
            .collect(Collectors.toUnmodifiableMap(Group::getId, Function.identity()));

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
        if (doesGroupExist(groupId)) {
            return Set.of(adminRole, viewerRole);
        }

        return Set.of();
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
    @SuppressWarnings("PMD.LawOfDemeter") // builder
    public List<Group> getUserGroups(Account account) throws MopsException {
        Map<Long, Group> newCache = new HashMap<>();
        for (long id : cachedGroups.keySet()) {
            Group group = cachedGroups.get(id);
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
    public Group getGroup(long groupId) throws MopsException {
        Group group = cachedGroups.get(groupId);
        if (group == null) {
            throw new DatabaseException("Die Gruppe konnte nicht gefunden werden");
        }

        return group;
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
