package mops.security;

import mops.businesslogic.Account;
import org.springframework.stereotype.Service;

@Service
public interface PermissionService {
    /**
     * @param account user credentials
     * @param groupId group id of the requested role
     */
    void fetchRoleForUserInGroup(Account account, long groupId);
}
