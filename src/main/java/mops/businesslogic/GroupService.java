package mops.businesslogic;

import mops.persistence.Directory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GroupService {
    /**
     * Fetches all root directories (one for each group) of one user.
     *
     * @param account the account the user
     * @return a list of group ids
     */
    List<Directory> getAllGroupRootDirectories(Account account);

    /**
     * Checks if the root folder exists and if not it will create it.
     *
     * @param account the user user account
     * @param groupId the id of the group
     */
    void createIfNotExists(Account account, long groupId);
}
