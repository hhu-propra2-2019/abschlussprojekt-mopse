package mops.businesslogic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * {@inheritDoc}
 * This is used during development and will return dummies.
 */
@Slf4j
@Service
@Profile({ "dev", "test" })
public class PermissionServiceDevImpl implements PermissionService {

    /**
     * Group id.
     */
    private static final Set<Long> VALID_GROUP_IDS = Set.of(100L);

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "PMD.LawOfDemeter", "PMD.OnlyOneReturn" }) // these are streams
    public String fetchRoleForUserInGroup(Account account, long groupId) {
        log.debug("Fetching roles for user '{}' with global roles '{}' in group '{}'.",
                account.getName(), account.getRoles(), groupId);

        if (VALID_GROUP_IDS.contains(groupId)) {
            if (account.getRoles().stream().anyMatch(role -> role.contains("admin"))) {
                log.debug("Found 'admin' global role, returning local role 'admin'.");
                return "admin";
            } else if (account.getRoles().stream()
                    .anyMatch(role -> role.contains("orga") || role.contains("korrektor"))) {
                log.debug("Found 'orga'/'korrektorin' global role, returning local role 'editor'.");
                return "editor";
            } else if (account.getRoles().stream().anyMatch(role -> role.contains("studentin"))) {
                log.debug("Returning local role 'viewer'.");
                return "viewer";
            }
        }

        log.debug("Returning local role 'outsider'.");
        return "outsider";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.OnlyOneReturn")
    public Set<String> fetchRolesInGroup(long groupId) {
        if (VALID_GROUP_IDS.contains(groupId)) {
            return Set.of("admin", "editor", "viewer");
        }

        return Set.of();
    }
}
