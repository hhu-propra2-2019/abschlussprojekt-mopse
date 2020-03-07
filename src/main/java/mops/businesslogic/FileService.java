package mops.businesslogic;

import mops.persistence.FileInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FileService {
    /**
     * Returns all files of a group.
     *
     * @param account user credentials
     * @param groupId group identification
     * @return list of all files in that directory
     */
    List<FileInfo> getAllFilesOfGroup(Account account, long groupId);

    /**
     * Searches for files in a group.
     *
     * @param account user credentials
     * @param groupId group identification for the group to be searched
     * @param query   a query which specifies the serach
     * @return a list of files
     */
    List<FileInfo> searchFilesInGroup(Account account, long groupId, FileQuery query);
}
