package mops.businesslogic.directory;

import mops.businesslogic.security.Account;
import mops.exception.MopsException;

import java.util.zip.ZipOutputStream;

/**
 * Zips a directory and all it's sub content.
 */
public interface ZipService {
    /**
     * Zips a directory and all it's content.
     *
     * @param account user credentials
     * @param dirId id of the directory to be zipped
     * @return {@link ZipOutputStream} output stream of the zipped content
     */
    ZipOutputStream zipDirectory(Account account, long dirId) throws MopsException;
}
