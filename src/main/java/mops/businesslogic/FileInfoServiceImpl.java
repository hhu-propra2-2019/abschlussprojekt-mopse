package mops.businesslogic;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mops.businesslogic.exception.DatabaseException;
import mops.exception.MopsException;
import mops.persistence.FileInfoRepository;
import mops.persistence.file.FileInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
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
            log.error("Failed to retrieve all file in directory with id %d from the database.", dirId);
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
            log.error("Failed to retrieve file info for file with id %d from the database.", fileId);
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
            log.error("Failed to save file '%s' of type '%s' with size '%d' bytes to database.",
                    fileInfo.getName(),
                    fileInfo.getType(),
                    fileInfo.getSize());
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
            log.error("Failed to delete file with id %d", fileId);
            throw new DatabaseException("File Info couldn't be deleted!", e);
        }
    }
}
