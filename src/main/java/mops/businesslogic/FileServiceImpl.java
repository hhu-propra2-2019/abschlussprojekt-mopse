package mops.businesslogic;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mops.businesslogic.exception.DeleteAccessPermissionException;
import mops.businesslogic.exception.FileNotFoundException;
import mops.businesslogic.exception.ReadAccessPermissionException;
import mops.businesslogic.exception.WriteAccessPermissionException;
import mops.exception.MopsException;
import mops.persistence.FileRepository;
import mops.persistence.directory.Directory;
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
        UserPermission userPermission = directoryService.getPermissionsOfUser(account, dirId);

        if (!userPermission.isWrite()) {
            log.error("User {} tried to save a file without write permission.",
                    account.getName()
            );
            throw new WriteAccessPermissionException("No write permission");
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
            log.error("Error while saving file {} by user {}. Error: {}",
                    meta.getName(),
                    account.getName(),
                    e.getMessage()
            );
            throw new MopsException("Error while saving", e);
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
            log.error("User {} tried to retrieved non existing file with ID {}. Error: {}",
                    account.getName(),
                    fileId,
                    e.getMessage()
            );
            throw new FileNotFoundException("File with ID " + fileId + " not found", e);
        }

        UserPermission userPermission = directoryService.getPermissionsOfUser(account, fileInfo.getDirectoryId());

        if (!userPermission.isRead()) {
            log.error("User {} tried to read file {} without permission.",
                    account.getName(),
                    fileId
            );
            throw new ReadAccessPermissionException("No read permission");
        }

        try (InputStream stream = fileRepository.getFileContent(fileId)) {
            ByteArrayResource byteArrayResource = new ByteArrayResource(stream.readAllBytes());
            return new FileContainer(fileInfo, byteArrayResource);
        } catch (MopsException | IOException e) {
            log.error("Error on retrieving file with ID {}. Error: {}",
                    fileId,
                    e.getMessage()
            );
            throw new MopsException("Error while retrieving", e);
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
            log.error("User {} tried to delete file with ID {}, bot file was not found.",
                    account.getName(),
                    fileId
            );
            throw new FileNotFoundException("File not found", e);
        }

        UserPermission userPermission = directoryService.getPermissionsOfUser(account, fileInfo.getDirectoryId());
        String owner = fileInfo.getOwner();
        boolean isOwner = owner.equals(account.getName());
        // Only true if user is not the owner and has no delete permission
        if (!isOwner && !userPermission.isDelete()) {
            log.error("User {} tried to delete file with ID {} without permission.",
                    account.getName(),
                    fileId
            );
            throw new DeleteAccessPermissionException("No delete permission");
        }

        try {
            fileInfoService.deleteFileInfo(fileId);
            fileRepository.deleteFile(fileId);
        } catch (MopsException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error("File with ID {} error on retrieving. Error: {}",
                    fileId,
                    e.getMessage()
            );
            throw new MopsException("Error while deleting", e);
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
            log.error("User {} tried to retrieved non existing file with ID {}. Error: {}",
                    account.getName(),
                    fileId,
                    e.getMessage()
            );
            throw new FileNotFoundException("File not found", e);
        }
        UserPermission userPermission = directoryService.getPermissionsOfUser(account, fileInfo.getDirectoryId());

        if (!userPermission.isRead()) {
            log.error("User {} tried to read file {} without permission.",
                    account.getName(),
                    fileId,
                    new ReadAccessPermissionException("No read permission")
            );
        }
        return fileInfoService.fetchFileInfo(fileId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.LawOfDemeter")
    public List<FileInfo> getFilesOfDirectory(Account account, long dirId) throws MopsException {
        UserPermission userPermission = directoryService.getPermissionsOfUser(account, dirId);
        if (!userPermission.isRead()) {
            log.error("User {} tried to read files in directory with ID {} without permission.",
                    account.getName(),
                    dirId,
                    new ReadAccessPermissionException("No read permission")
            );
        }
        return fileInfoService.fetchAllFilesInDirectory(dirId);
    }
}
