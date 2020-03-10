package mops.security;

import mops.businesslogic.Account;
import mops.persistence.directory.Directory;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * API for GruppenFindung which handles permissions.
 */
@Service
public interface PermissionService {
    /**
     * @param account   user credentials
     * @param directory directory object
     * @return a list of directory permission entries
     */
    Set<String> fetchRoleForUserInDirectory(Account account, Directory directory);
}
