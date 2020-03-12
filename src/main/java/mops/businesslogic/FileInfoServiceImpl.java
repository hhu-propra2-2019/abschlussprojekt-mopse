package mops.businesslogic;

import lombok.AllArgsConstructor;
import mops.exception.MopsException;
import mops.persistence.FileInfoRepository;
import mops.persistence.file.FileInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class FileInfoServiceImpl implements FileInfoService {

    /**
     * reference to fileInfoRepository.
     */
    private final FileInfoRepository fileInfoRepo;

    /**
     * @param dirId directory id
     * @return a list of files in that directory
     */
    @Override
    public List<FileInfo> fetchAllFilesInDirectory(long dirId) {
        return fileInfoRepo.getAllFileInfoByDirectory(dirId);
    }

    /**
     * @param fileId file id
     * @return a FileInfo object
     */
    @Override
    public FileInfo fetchFileInfo(long fileId) {
        return fileInfoRepo.getFileInfoById(fileId);
    }

    /**
     * @param fileInfo Metadata of a file
     * @return ID the FileInfo was saved under
     */
    @Override
    public long saveFileInfo(FileInfo fileInfo) {
        return fileInfoRepo.addFileInfoToDatabase(fileInfo);
    }

    /**
     * @param fileId file id
     */
    @Override
    public void deleteFileInfo(long fileId) throws MopsException {
        fileInfoRepo.deleteFileInfoFromDatabase(fileId);
    }

    /**
     * @param fileId file id
     * @return dir id
     */
    @Override
    public long fetchDirectoryId(long fileId) {
        return fileInfoRepo.getDirectoryIdByFileId(fileId);
    }

}
