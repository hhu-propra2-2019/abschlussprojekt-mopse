package mops.businesslogic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Profile("dev")
public class PermissionServiceDevImpl implements PermissionService {

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "PMD.LawOfDemeter", "PMD.OnlyOneReturn" }) // these are streams
    public String fetchRoleForUserInGroup(Account account, long groupId) {
        log.debug("Fetching roles for user '{}' with global roles '{}' in group '{}'.",
                account.getName(), account.getRoles(), groupId);
        if (account.getRoles().stream().anyMatch(role -> role.contains("admin"))) {
            log.debug("Found 'admin' global role, returning local role 'admin'.");
            return "admin";
        } else if (account.getRoles().stream().anyMatch(role -> role.contains("orga") || role.contains("korrektor"))) {
            log.debug("Found 'orga'/'korrektorin' global role, returning local role 'editor'.");
            return "editor";
        } else if (account.getName().length() % 2 == groupId % 2) {
            log.debug("Returning local role 'viewer'.");
            return "viewer";
        }

        log.debug("Returning local role 'outsider'.");
        return "outsider";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> fetchRolesInGroup(long groupId) {
        return Set.of("admin", "editor", "viewer");
    }

    @Override
    public List<Group> fetchGroupsForUser() {
        return null;
    }
}
