package mops.businesslogic;

import lombok.AllArgsConstructor;
import mops.businesslogic.exception.DatabaseException;
import mops.exception.MopsException;
import mops.persistence.FileInfoRepository;
import mops.persistence.file.FileInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class FileInfoServiceImpl implements FileInfoService {

    /**
     * Access to the FileInfo database.
     */
    private final FileInfoRepository fileInfoRepo;

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    @Override
    public List<FileInfo> fetchAllFilesInDirectory(long dirId) throws MopsException {
        try {
            return fileInfoRepo.findAllInDirectory(dirId);
        } catch (Exception e) {
            throw new DatabaseException("Couldn't find all files in directory!", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({ "PMD.LawOfDemeter", "PMD.AvoidCatchingGenericException" })
    @Override
    public FileInfo fetchFileInfo(long fileId) throws MopsException {
        try {
            return fileInfoRepo.findById(fileId).orElseThrow();
        } catch (Exception e) {
            throw new DatabaseException("Couldn't find File Info!", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    @Override
    public FileInfo saveFileInfo(FileInfo fileInfo) throws MopsException {
        try {
            return fileInfoRepo.save(fileInfo);
        } catch (Exception e) {
            throw new DatabaseException("File Info couldn't be saved!", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    @Override
    public void deleteFileInfo(long fileId) throws MopsException {
        try {
            fileInfoRepo.deleteById(fileId);
        } catch (Exception e) {
            throw new DatabaseException("File Info couldn't be deleted!", e);
        }
    }
}
