package mops.businesslogic;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mops.businesslogic.exception.DatabaseException;
import mops.exception.MopsException;
import mops.persistence.FileInfoRepository;
import mops.persistence.file.FileInfo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

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
            log.error("Failed to retrieve all files in directory with id {} from the database.", dirId);
            throw new DatabaseException("Es konnten nicht alle Dateien im Verzeichnis gefunden werden!", e);
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
            log.error("Failed to retrieve file info for file with id {} from the database.", fileId);
            throw new DatabaseException("Die Datei-Informationen konnten nicht gefunden werden!", e);
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
            log.error("Failed to save file '{}' of type '{}' with size '{}' bytes to database.",
                    fileInfo.getName(),
                    fileInfo.getType(),
                    fileInfo.getSize());
            throw new DatabaseException("Datei-Informationen konnten nicht gespeichert werden!", e);
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
            log.error("Failed to delete file with id {}.", fileId);
            throw new DatabaseException("Datei-Informationen konnten nicht gelöscht werden!", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public long getStorageUsageInGroup(long groupId) throws MopsException {
        try {
            return fileInfoRepo.getStorageUsageInGroup(groupId);
        } catch (Exception e) {
            log.error("Failed to get total storage used by group with id {}.", groupId);
            throw new DatabaseException("Gesamtspeicherplatz konnte nicht geladen werden!", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public long getTotalStorageUsage() throws MopsException {
        try {
            return fileInfoRepo.getTotalStorageUsage();
        } catch (Exception e) {
            log.error("Failed to get total storage used.");
            throw new DatabaseException("Gesamtspeicherplatz konnte nicht geladen werden!", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public long getFileCountInGroup(long groupId) throws MopsException {
        try {
            return fileInfoRepo.getFileCountInGroup(groupId);
        } catch (Exception e) {
            log.error("Failed to get total file count in group with id {}.", groupId);
            throw new DatabaseException("Gesamtdateianzahl konnte nicht geladen werden!", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public long getTotalFileCount() throws MopsException {
        try {
            return fileInfoRepo.count();
        } catch (Exception e) {
            log.error("Failed to get total file count.");
            throw new DatabaseException("Gesamtdateianzahl konnte nicht geladen werden!", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public Set<Long> fetchAllFileInfoIds() throws MopsException {
        try {
            return fileInfoRepo.findAllIds();
        } catch (Exception e) {
            log.error("Failed to get all FileInfo ids.");
            throw new MopsException("IDs konnten nicht gefunden werden.", e);
        }
    }
}
