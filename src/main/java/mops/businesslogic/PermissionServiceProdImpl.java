package mops.businesslogic;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Profile("prod")
public class PermissionServiceProdImpl implements PermissionService {

    /**
     * {@inheritDoc}
     */
    @Override
    public String fetchRoleForUserInGroup(Account account, long groupId) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> fetchRolesInGroup(long groupId) {
        throw new UnsupportedOperationException("not yet implemented");
    }
}
