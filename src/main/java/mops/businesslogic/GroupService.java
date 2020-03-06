package mops.businesslogic;

import mops.Account;
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
}
