package mops.businesslogic;

import mops.persistence.file.FileInfo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Handles meta data for files.
 */
@Service
public interface FileInfoService {

    /**
     * @param dirId directory id
     * @return a list of files in that directory
     */
    List<FileInfo> fetchAllFilesInDirectory(long dirId);

}
