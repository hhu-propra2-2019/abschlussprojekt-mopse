package mops.businesslogic;

import mops.exception.MopsException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GroupService {

    /**
     * Fetches all visible groups of one user.
     *
     * @param account the account the user
     * @return a list of groups
     */
    List<Group> getAllGroups(Account account) throws MopsException;

    /**
     * @param account user account
     * @param groupId the id of the group
     * @return a wrapper for group urls
     */
    GroupDirUrlWrapper getGroupUrl(Account account, long groupId) throws MopsException;

}
