package mops.businesslogic.delete;

import mops.businesslogic.security.Account;
import mops.exception.MopsException;
import mops.persistence.directory.Directory;
import org.springframework.stereotype.Service;

/**
 * Handles directory and file deletion.
 */
@Service
public interface DeleteService {

    /**
     * Deletes folder recursively.
     *
     * @param account     user credentials
     * @param dirId id of the folder
     * @return parent directory
     */
    Directory deleteFolder(Account account, long dirId) throws MopsException;

}
