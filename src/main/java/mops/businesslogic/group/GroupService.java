package mops.businesslogic.group;

import mops.businesslogic.security.Account;
import mops.exception.MopsException;
import mops.persistence.group.Group;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * API to Gruppenfindung.
 */
@Service
public interface GroupService {

    /**
     * Tests whether a group exists.
     *
     * @param groupId the id of the group
     * @return true if it exists, false otherwise
     */
    boolean doesGroupExist(UUID groupId) throws MopsException;

    /**
     * Gets all roles that exist in a group.
     *
     * @param groupId the id of the group
     * @return gets all roles of that group
     */
    Set<String> getRoles(UUID groupId) throws MopsException;

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
    List<Group> getUserGroups(Account account) throws MopsException;

    /**
     * Get a group by id.
     *
     * @param groupId goup id
     * @return group
     */
    Group getGroup(UUID groupId) throws MopsException;

}
