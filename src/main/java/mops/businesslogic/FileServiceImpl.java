package mops.businesslogic;

import mops.businesslogic.exception.WriteAccessPermissionException;
import mops.exception.MopsException;
import mops.persistence.FileRepository;
import mops.persistence.directory.Directory;
import mops.persistence.file.FileInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Service
public class FileServiceImpl implements FileService {

    /**
     * Service for permission checks.
     */
    private final DirectoryService directoryService;
    /**
     * Service for saving and retrieving file meta data.
     */
    private final FileInfoService fileInfoService;
    /**
     * File content repository.
     */
    private final FileRepository fileRepository;

    /**
     *  Constructor.
     *
     * @param directoryService Service for permission checks.
     * @param fileInfoService Service for saving and retrieving file meta data.
     * @param fileRepository File content repository.
     */
    public FileServiceImpl(DirectoryService directoryService, FileInfoService fileInfoService,
                           FileRepository fileRepository) {
        this.directoryService = directoryService;
        this.fileInfoService = fileInfoService;
        this.fileRepository = fileRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = MopsException.class)
    public void saveFile(Account account, long dirId, MultipartFile multipartFile,
                         Set<String> tags) throws MopsException {

        UserPermission userPermission = directoryService.getPermissionsOfUser(account, dirId);

        if (!userPermission.isWrite()) {
            throw new WriteAccessPermissionException("Keine Schreibberechtigung");
        }

        FileInfo meta = FileInfo.builder()
                .from(multipartFile)
                .directory(dirId)
                .owner(account.getName())
                .tags(tags)
                .build();

        long fileId;

        try {
            FileInfo fileInfo = fileInfoService.saveFileInfo(meta);
            fileRepository.saveFile(multipartFile, fileInfo.getId());
        } catch (MopsException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new MopsException("Fehler beim speichern.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FileContainer getFile(Account account, long fileId) throws MopsException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Directory deleteFile(Account account, long fileId) throws MopsException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FileInfo getFileInfo(Account account, long fileId) throws MopsException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FileInfo> getFilesOfDirectory(Account account, long dirId) throws MopsException {
        return null;
    }
}
