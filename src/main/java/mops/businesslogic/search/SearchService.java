package mops.businesslogic.search;

import mops.businesslogic.file.FileListEntry;
import mops.businesslogic.file.query.FileQuery;
import mops.businesslogic.security.Account;
import mops.exception.MopsException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for searching.
 */
@Service
public interface SearchService {

    /**
     * Searches a folder for files.
     *
     * @param account user credentials
     * @param dirId   id of the folder to be searched
     * @param query   wrapper object of the query parameter
     * @return list of files
     */
    List<FileListEntry> searchFolder(Account account, long dirId, FileQuery query) throws MopsException;

}
