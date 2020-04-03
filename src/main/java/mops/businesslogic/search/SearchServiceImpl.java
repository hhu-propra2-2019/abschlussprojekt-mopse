package mops.businesslogic.search;

import lombok.RequiredArgsConstructor;
import mops.businesslogic.directory.DirectoryService;
import mops.businesslogic.file.FileListEntry;
import mops.businesslogic.file.FileService;
import mops.businesslogic.file.query.FileQuery;
import mops.businesslogic.security.Account;
import mops.exception.MopsException;
import mops.persistence.directory.Directory;
import mops.persistence.file.FileInfo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    /**
     * File access.
     */
    private final FileService fileService;
    /**
     * Directory access.
     */
    private final DirectoryService directoryService;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FileListEntry> searchFolder(Account account, long dirId, FileQuery query) throws MopsException {
        List<FileListEntry> results = new ArrayList<>();
        doSearchFolder(account, dirId, query, results);
        results.sort(Comparator.comparing(FileListEntry::getFileInfo, FileInfo.NAME_COMPARATOR));
        return results;
    }

    /**
     * Internal search method for easier recursion.
     *
     * @param account user
     * @param dirId   directory
     * @param query   search query
     * @param results results list to be filled
     * @throws MopsException on error
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    private void doSearchFolder(Account account, long dirId, FileQuery query, List<FileListEntry> results)
            throws MopsException {
        fileService.getFilesOfDirectory(account, dirId).stream() // this is a stream not violation of demeter's law
                .filter(file -> query.checkMatch(file.getFileInfo()))
                .forEach(results::add);

        for (Directory subDir : directoryService.getSubFolders(account, dirId)) {
            doSearchFolder(account, subDir.getId(), query, results);
        }
    }
}
