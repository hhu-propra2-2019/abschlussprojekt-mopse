package mops.security;

import mops.businesslogic.Account;
import mops.persistence.directory.Directory;
import mops.persistence.permission.DirectoryPermissionEntry;
import org.springframework.stereotype.Service;

import java.util.List;

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
    List<DirectoryPermissionEntry> fetchRoleForUserInGroup(Account account, Directory directory);
}
