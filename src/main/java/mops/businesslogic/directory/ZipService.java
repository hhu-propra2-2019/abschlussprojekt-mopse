package mops.businesslogic.directory;

import mops.businesslogic.security.Account;
import mops.exception.MopsException;
import org.springframework.stereotype.Service;

import java.io.OutputStream;

/**
 * Zips a directory and all its sub content.
 */
@Service
public interface ZipService {

    /**
     * Zips a directory and all its contents.
     *
     * @param account      user credentials
     * @param dirId        id of the directory to be zipped
     * @param outputStream output stream to write the zipped contents to
     */
    void zipDirectory(Account account, long dirId, OutputStream outputStream) throws MopsException;

}
