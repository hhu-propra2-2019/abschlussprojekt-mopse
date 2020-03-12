package mops.businesslogic;

import lombok.AllArgsConstructor;
import mops.exception.MopsException;
import mops.persistence.FileInfoRepository;
import mops.persistence.file.FileInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class FIleInfoServiceImpl implements FileInfoService {

    private final FileInfoRepository fileInfoRepository;

    /**
     * @param dirId directory id
     * @return a list of files in that directory
     */
    public List<FileInfo> fetchAllFilesInDirectory(long dirId) {
        return fileInfoRepository.getAllFileInfoByDirectory(dirId);
    }

    /**
     * @param fileId file id
     * @return a FileInfo object
     */
    public FileInfo fetchFileInfo(long fileId) {
        return fileInfoRepository.getFileInfoById(fileId);
    }

    /**
     * @param fileInfo Metadata of a file
     * @return ID the FileInfo was saved under
     */
    public long saveFileInfo(FileInfo fileInfo) {
        return fileInfoRepository.addFileInfoToDatabase(fileInfo);
    }

    /**
     * @param fileId file id
     */
    public void deleteFileInfo(long fileId) throws MopsException {
        fileInfoRepository.deleteFileInfoFromDatabase(fileId);
    }

    /**
     * @param fileId file id
     * @return dir id
     */
    public long fetchDirectoryId(long fileId) {
        return fileInfoRepository.getDirectoryIdByFileId(fileId);
    }
}
