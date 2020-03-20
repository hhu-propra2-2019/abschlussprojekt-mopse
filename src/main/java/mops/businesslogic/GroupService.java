package mops.businesslogic;

import mops.exception.MopsException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Handles requests for group meta data.
 */
@Service
public interface GroupService {

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

    /**
     * Gets the group url.
     * @param groupId the id of the group
     * @return a wrapper for group urls
     */
    GroupRootDirWrapper getGroupUrl(long groupId) throws MopsException;

}
