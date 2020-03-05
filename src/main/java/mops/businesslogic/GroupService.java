package mops.businesslogic;

import mops.persistence.Directory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GroupService {
    /**
     * fetches all root directories (groups) of one user.
     *
     * @param userId the account id of the user
     * @return a list of group ids
     */
    List<Directory> getAllGroups(int userId);
}
