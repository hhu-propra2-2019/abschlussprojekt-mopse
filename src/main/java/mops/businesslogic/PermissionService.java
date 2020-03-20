package mops.businesslogic;

import mops.exception.MopsException;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * API for GruppenFindung which handles permissions.
 */
@Service
public interface PermissionService {

    /**
     * Gets the role for one user in a group.
     * @param account user credentials
     * @param groupId the id of the group
     * @return the role of the user in that group
     */
    String fetchRoleForUserInGroup(Account account, long groupId) throws MopsException;

    /**
     * Gets all roles for a group.
     * @param groupId the id of the group
     * @return gets all roles of that group
     */
    Set<String> fetchRolesInGroup(long groupId) throws MopsException;

}
