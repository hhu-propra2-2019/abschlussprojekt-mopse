package mops.businesslogic;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Profile("dev")
public class PermissionServiceDevImpl implements PermissionService {

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "PMD.LawOfDemeter", "PMD.OnlyOneReturn" }) // these are streams
    public String fetchRoleForUserInGroup(Account account, long groupId) {
        if (account.getRoles().stream().anyMatch(role -> role.contains("admin"))) {
            return "admin";
        } else if (account.getRoles().stream().anyMatch(role -> role.contains("orga") || role.contains("korrektor"))) {
            return "editor";
        } else if (account.getName().length() % 2 == groupId % 2) {
            return "viewer";
        }
        return "outsider";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> fetchRolesInGroup(long groupId) {
        return Set.of("admin", "editor", "viewer");
    }
}
