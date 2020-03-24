package mops.businesslogic.group;

import lombok.extern.slf4j.Slf4j;
import mops.businesslogic.security.Account;
import mops.exception.MopsException;
import mops.persistence.group.Group;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
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
    @SuppressWarnings({ "PMD.LawOfDemeter", "PMD.OnlyOneReturn" }) // these are streams
    public String getUserRole(Account account, long groupId) {
        log.debug("Fetching group role for user '{}' with keycloak roles '{}' in group '{}'.",
                account.getName(), account.getRoles(), groupId);

        if (VALID_GROUP_IDS.contains(groupId)) {
            if (account.getRoles().stream().anyMatch(role -> role.contains("admin"))) {
                log.debug("Found 'admin' keycloak role, returning group role 'admin'.");
                return adminRole;
            } else if (account.getRoles().stream()
                    .anyMatch(role -> role.contains("orga") || role.contains("korrektor"))) {
                log.debug("Found 'orga'/'korrektorin' keycloak role, returning group role 'admin'.");
                return adminRole;
            } else if (account.getRoles().stream().anyMatch(role -> role.contains("studentin"))) {
                log.debug("Returning group role 'viewer'.");
                return viewerRole;
            }
        }

        log.debug("Returning group role 'outsider'.");
        return "outsider";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.OnlyOneReturn")
    public Set<String> getRoles(long groupId) {
        if (VALID_GROUP_IDS.contains(groupId)) {
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
        return VALID_GROUP_IDS.stream()
                .map(id -> Group.builder().id(id).name("Einzigen #" + id).build())
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Group> getUserGroups(Account account) throws MopsException {
        return getAllGroups(); // every user is in every group
    }
}
