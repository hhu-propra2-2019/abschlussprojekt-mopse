package mops.security;

import mops.businesslogic.Account;
import mops.businesslogic.GroupRole;
import mops.persistence.directory.Directory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * API for GruppenFindung which handles permissions.
 */
@Service
public interface PermissionService {
    /**
     * @param account   user credentials
     * @param directory directory object
     */
    List<GroupRole> fetchRoleForUserInGroup(Account account, Directory directory);
}
