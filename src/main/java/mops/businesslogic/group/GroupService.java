package mops.businesslogic.group;

import mops.businesslogic.security.Account;
import mops.exception.MopsException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * API to Gruppenfindung.
 */
@Service
public interface GroupService {

    /**
     * Gets the role for one user in a group.
     *
     * @param account user credentials
     * @param groupId the id of the group
     * @return the role of the user in that group
     */
    String fetchRoleForUserInGroup(Account account, long groupId) throws MopsException;

    /**
     * Gets all roles for a group.
     *
     * @param groupId the id of the group
     * @return gets all roles of that group
     */
    Set<String> fetchRolesInGroup(long groupId) throws MopsException;

    /**
     * Fetches all groups.
     *
     * @return a list of groups
     */
    List<Group> getAllGroups() throws MopsException;

    /**
     * Fetches all visible groups of one user.
     *
     * @param account the account the user
     * @return a list of groups
     */
    List<Group> getAllGroupsOfUser(Account account) throws MopsException;

}
