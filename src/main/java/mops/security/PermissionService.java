package mops.security;

import mops.businesslogic.Account;
import mops.persistence.directory.Directory;
import org.springframework.stereotype.Service;

@Service
public interface PermissionService {
    /**
     * @param account   user credentials
     * @param directory directory object
     */
    void fetchRoleForUserInGroup(Account account, Directory directory);
}
