package mops.businesslogic;

import mops.persistence.Directory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GroupService {
    /**
     * Fetches all root directories (one for each group) of one user.
     *
     * @param userId the account id of the user
     * @return a list of group ids
     */
    List<Directory> getAllGroups(int userId);
}
