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
     * {@inheritDoc}
     */
    @Override
    public List<FileInfo> fetchAllFilesInDirectory(long dirId) throws MopsException {
        return fileInfoRepo.getAllFileInfoByDirectory(dirId);
    }

    /**
     * @param fileId file id
     * @return a FileInfo object
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    @Override
    public FileInfo fetchFileInfo(long fileId) throws MopsException {
        return fileInfoRepo.findById(fileId).orElseThrow(() -> new MopsException("..."));
    }

    /**
     * @param fileInfo Metadata of a file
     * @return ID the FileInfo was saved under
     */
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    @Override
    public FileInfo saveFileInfo(FileInfo fileInfo) throws MopsException {
        try {
            return fileInfoRepo.save(fileInfo);
        } catch (Exception e) {
            //TODO: better exception
            throw new MopsException("...", e);
        }
    }

    /**
     * @param fileId file id
     */
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    @Override
    public void deleteFileInfo(long fileId) throws MopsException {
        try {
            fileInfoRepo.deleteById(fileId);
        } catch (Exception e) {
            //TODO: better exception
            throw new MopsException("...", e);
        }
    }
}
