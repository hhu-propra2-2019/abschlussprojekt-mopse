package mops.businesslogic.group;

import mops.businesslogic.security.Account;
import mops.exception.MopsException;
import mops.persistence.group.Group;
import mops.persistence.permission.DirectoryPermissions;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * API to Gruppenfindung.
 */
@Service
public interface GroupService {

    /**
     * Gets all roles that exist in a group.
     *
     * @param groupId the id of the group
     * @return gets all roles of that group
     */
    Set<String> getRoles(long groupId) throws MopsException;

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
     * Creates the default permissions.
     *
     * @param groupId group id
     * @return default directory permissions
     */
    DirectoryPermissions getDefaultPermissions(long groupId);

    /**
     * Get a group by id.
     *
     * @param groupId group id
     * @return group
     */
    Group getGroup(long groupId) throws MopsException;

    /**
     * Get a group by its group uuid.
     *
     * @param groupId group uuid
     * @return group
     */
    Optional<Group> findGroupByGroupId(UUID groupId) throws MopsException;

    /**
     * Save the given group.
     *
     * @param group group to be saved
     * @return saved group
     */
    Group saveGroup(Group group) throws MopsException;

    /**
     * Delete the given group.
     *
     * @param groupId id of the group to be deleted
     */
    void deleteGroup(long groupId) throws MopsException;

}
