package mops.security;

import mops.businesslogic.Account;
import mops.persistence.directory.Directory;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * API for GruppenFindung which handles permissions.
 */
@Service
public interface PermissionService {
    /**
     * @param account   user credentials
     * @param directory directory object
     * @return the role of the user in the group of the directory
     */
    String fetchRoleForUserInDirectory(Account account, Directory directory);

    /**
     * @param account user credentials
     * @param groupId the id of the group
     * @return the role of the user in that group
     */
    String fetchRoleForUserInGroup(Account account, Long groupId);

    /**
     * @param groupId the id of the group
     * @return gets all roles of that group
     */
    Set<String> fetchRolesInGroup(Long groupId);
}
