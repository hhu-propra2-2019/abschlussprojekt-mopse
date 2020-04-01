package mops.businesslogic.file;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mops.businesslogic.directory.DirectoryService;
import mops.businesslogic.exception.*;
import mops.businesslogic.security.Account;
import mops.businesslogic.security.SecurityService;
import mops.businesslogic.security.UserPermission;
import mops.businesslogic.time.TimeService;
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
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
     * Queries the time.
     */
    private final TimeService timeService;
    /**
     * File content repository.
     */
    private final FileRepository fileRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    // builder & too many if-statements - but those are fine as they are exit points of this method
    @SuppressWarnings({ "PMD.LawOfDemeter", "PMD.CyclomaticComplexity" })
    public void saveFile(Account account, long dirId, MultipartFile multipartFile,
                         Set<String> tags) throws MopsException {
        if (multipartFile.getSize() <= 0) {
            log.error("User {} tried to save a file that was empty.", account.getName());
            throw new StorageException("Leere Datei");
        }

        String name = multipartFile.getOriginalFilename();
        if (name == null || name.isEmpty()) {
            log.error("User {} tried to save a file with an empty name.", account.getName());
            throw new StorageException("Name leer.");
        }

        Directory directory = directoryService.getDirectory(dirId);
        UserPermission userPermission = securityService.getPermissionsOfUser(account, directory);

        if (!userPermission.isWrite()) {
            log.error("User {} tried to save a file without write permission.",
                    account.getName()
            );
            throw new WriteAccessPermissionException("Keine Schreibberechtigung");
        }

        FileInfo meta = FileInfo.builder()
                .from(multipartFile)
                .directory(dirId)
                .owner(account.getName())
                .tags(tags)
                .build();

        try {
            FileInfo fileInfo = fileInfoService.saveFileInfo(meta);
            fileRepository.saveFile(multipartFile, fileInfo.getId());
        } catch (DatabaseDuplicationException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error("Error while saving file {} by user {}:",
                    meta.getName(),
                    account.getName(),
                    e
            );
            throw new MopsException("Die Datei ist schon vorhanden.", e);
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

        boolean isOwner = fileInfo.getOwner().equals(account.getName());
        boolean isAdmin = securityService.isUserAdmin(account, directory.getGroupOwner());
        boolean isAvailable = fileInfo.isAvailable(timeService.getInstantNow());
        boolean isPrivileged = isOwner || isAdmin;
        boolean normalReadAllowed = userPermission.isRead() && isAvailable;

        if (!isPrivileged && !normalReadAllowed) {
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
    @Transactional
    @SuppressWarnings({ "PMD.LawOfDemeter", "PMD.DataflowAnomalyAnalysis" })
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
    @SuppressWarnings({ "PMD.LawOfDemeter", "PMD.DataflowAnomalyAnalysis" })
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

        boolean isAdmin = securityService.isUserAdmin(account, directory.getGroupOwner());
        Instant now = timeService.getInstantNow();

        return fileInfoService.fetchAllFilesInDirectory(dirId).stream()
                .filter(file -> isAdmin || account.getName().equals(file.getOwner()) || file.isAvailable(now))
                .collect(Collectors.toList());
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

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "PMD.LawOfDemeter", "PMD.AvoidLiteralsInIfCondition", "PMD.AvoidReassigningParameters" })
    public Directory renameFile(Account account, long fileId, String newName) throws MopsException {
        if (newName.isEmpty()) {
            log.error("User {} tried to rename a file without a name.",
                    account.getName()
            );
            throw new EmptyNameException("Der Dateiname darf nicht leer sein.");
        }

        newName = newName.replaceAll("[^a-zA-Z0-9._-]", "_");

        FileInfo fileInfo = fileInfoService.fetchFileInfo(fileId);
        Directory directory = directoryService.getDirectory(fileInfo.getDirectoryId());
        UserPermission permissionsOfUser = securityService.getPermissionsOfUser(account, directory);

        if (!permissionsOfUser.isDelete() || !permissionsOfUser.isWrite()) {
            log.error("User {} tried to rename a file without write and delete permission.",
                    account.getName()
            );
            throw new WriteAccessPermissionException("Keine Schreibberechtigung und Löschberechtigung");
        }

        String[] fileNameParts = fileInfo.getName().split("\\.");
        if (fileNameParts.length > 1) {
            String fileExtension = "." + fileNameParts[fileNameParts.length - 1];
            fileInfo.setName(newName + fileExtension);
        } else {
            // file had no extension
            fileInfo.setName(newName);
        }

        fileInfoService.saveFileInfo(fileInfo);
        return directory;
    }
}
