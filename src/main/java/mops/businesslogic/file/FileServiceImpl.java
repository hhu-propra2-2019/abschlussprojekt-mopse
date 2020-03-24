package mops.businesslogic.file;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mops.businesslogic.directory.DirectoryService;
import mops.businesslogic.exception.DeleteAccessPermissionException;
import mops.businesslogic.exception.FileNotFoundException;
import mops.businesslogic.exception.ReadAccessPermissionException;
import mops.businesslogic.exception.WriteAccessPermissionException;
import mops.businesslogic.security.Account;
import mops.businesslogic.security.SecurityService;
import mops.businesslogic.security.UserPermission;
import mops.exception.MopsException;
import mops.persistence.FileRepository;
import mops.persistence.directory.Directory;
import mops.persistence.exception.StorageException;
import mops.persistence.file.FileInfo;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

/**
 * Handles requests to MinIO.
 */
@Service
@AllArgsConstructor
@Slf4j
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
     * Handle permission checks for roles.
     */
    private final SecurityService securityService;
    /**
     * File content repository.
     */
    private final FileRepository fileRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.LawOfDemeter")
    @Transactional(rollbackFor = MopsException.class)
    public void saveFile(Account account, long dirId, MultipartFile multipartFile,
                         Set<String> tags) throws MopsException {
        Directory directory = directoryService.getDirectory(dirId);
        UserPermission userPermission = securityService.getPermissionsOfUser(account, directory);

        if (!userPermission.isWrite()) {
            log.error("User {} tried to save a file without write permission.",
                    account.getName()
            );
            throw new WriteAccessPermissionException("Keine Schreibberechtigung");
        }
        if(multipartFile.getSize() <= 0) {
            log.error("User {} tried to save a file that was empty.",
                    account.getName()
            );
            throw new StorageException("Leere Datei");
        }
        //no Law of demeter violation
        FileInfo meta = FileInfo.builder()
                .from(multipartFile)
                .directory(dirId)
                .owner(account.getName())
                .tags(tags)
                .build();

        try {
            FileInfo fileInfo = fileInfoService.saveFileInfo(meta);
            fileRepository.saveFile(multipartFile, fileInfo.getId());
        } catch (MopsException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error("Error while saving file {} by user {}:",
                    meta.getName(),
                    account.getName(),
                    e
            );
            throw new MopsException("Fehler während des Speicherns aufgetreten", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "PMD.LawOfDemeter", "PMD.DataflowAnomalyAnalysis" })
    @SuppressFBWarnings(value = "RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE",
            justification = "There's no nullcheck here.")
    public FileContainer getFile(Account account, long fileId) throws MopsException {
        FileInfo fileInfo;
        try {
            fileInfo = fileInfoService.fetchFileInfo(fileId);
        } catch (MopsException e) {
            log.error("User {} tried to retrieve non existing file with ID {}:",
                    account.getName(),
                    fileId,
                    e
            );
            throw new FileNotFoundException(String.format("Datei mit ID %d wurde nicht gefunden", fileId), e);
        }

        Directory directory = directoryService.getDirectory(fileInfo.getDirectoryId());
        UserPermission userPermission = securityService.getPermissionsOfUser(account, directory);

        if (!userPermission.isRead()) {
            log.error("User {} tried to read file {} without permission.",
                    account.getName(),
                    fileId
            );
            throw new ReadAccessPermissionException("Keine Leseberechtigung");
        }

        try (InputStream stream = fileRepository.getFileContent(fileId)) {
            ByteArrayResource byteArrayResource = new ByteArrayResource(stream.readAllBytes());
            return new FileContainer(fileInfo, byteArrayResource);
        } catch (MopsException | IOException e) {
            log.error("Error on retrieving file with ID {}:",
                    fileId,
                    e
            );
            throw new MopsException("Fehler während des Abrufens aufgetreten", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "PMD.LawOfDemeter", "PMD.DataflowAnomalyAnalysis" })
    @Transactional(rollbackFor = MopsException.class)
    public Directory deleteFile(Account account, long fileId) throws MopsException {
        FileInfo fileInfo;
        try {
            fileInfo = fileInfoService.fetchFileInfo(fileId);
        } catch (MopsException e) {
            log.error("User {} tried to delete file with ID {}, but file was not found:",
                    account.getName(),
                    fileId,
                    e
            );
            throw new FileNotFoundException("Datei wurde nicht gefunden", e);
        }

        Directory directory = directoryService.getDirectory(fileInfo.getDirectoryId());
        UserPermission userPermission = securityService.getPermissionsOfUser(account, directory);
        String owner = fileInfo.getOwner();
        boolean isOwner = owner.equals(account.getName());
        // Only true if user is not the owner and has no delete permission
        if (!isOwner && !userPermission.isDelete()) {
            log.error("User {} tried to delete file with ID {} without permission.",
                    account.getName(),
                    fileId
            );
            throw new DeleteAccessPermissionException("Keine Löschberechtigung");
        }

        try {
            fileInfoService.deleteFileInfo(fileId);
            fileRepository.deleteFile(fileId);
        } catch (MopsException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error("File with ID {} error on deleting:",
                    fileId,
                    e
            );
            throw new MopsException("Fehler während des Löschens aufgetreten", e);
        }
        return directoryService.getDirectory(fileInfo.getDirectoryId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "PMD.LawOfDemeter", "PMD.DataflowAnomalyAnalysis" })
    public FileInfo getFileInfo(Account account, long fileId) throws MopsException {
        FileInfo fileInfo;
        try {
            fileInfo = fileInfoService.fetchFileInfo(fileId);
        } catch (MopsException e) {
            log.error("User {} tried to retrieve non existing file with ID {}:",
                    account.getName(),
                    fileId,
                    e
            );
            throw new FileNotFoundException("Datei nicht gefunden", e);
        }

        Directory directory = directoryService.getDirectory(fileInfo.getDirectoryId());
        UserPermission userPermission = securityService.getPermissionsOfUser(account, directory);

        if (!userPermission.isRead()) {
            log.error("User {} tried to read file {} without permission.",
                    account.getName(),
                    fileId
            );
            throw new ReadAccessPermissionException("Keine Leseberechtigung");
        }
        return fileInfoService.fetchFileInfo(fileId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.LawOfDemeter")
    public List<FileInfo> getFilesOfDirectory(Account account, long dirId) throws MopsException {
        Directory directory = directoryService.getDirectory(dirId);
        UserPermission userPermission = securityService.getPermissionsOfUser(account, directory);
        if (!userPermission.isRead()) {
            log.error("User {} tried to read files in directory with ID {} without permission.",
                    account.getName(),
                    dirId
            );
            throw new ReadAccessPermissionException("Keine Leseberechtigung");
        }
        return fileInfoService.fetchAllFilesInDirectory(dirId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Long> getAllFileIds() throws MopsException {
        return fileRepository.getAllIds();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteFile(long fileId) throws MopsException {
        fileRepository.deleteFile(fileId);
    }
}
