package mops.businesslogic;

import mops.persistence.File;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FileService {
    /**
     * Returns all files of a group.
     *
     * @param groupId group identification
     * @return list of all files in that directory
     */
    List<File> getAllFilesOfGroup(int groupId);
}
