package mops.businesslogic;

import org.springframework.stereotype.Service;

@Service
public interface GroupService {
    /**
     * fetches all groups of one user.
     *
     * @param userId the account id of the user
     * @return a list of group ids
     */
    int[] getAllGroups(int userId);
}
