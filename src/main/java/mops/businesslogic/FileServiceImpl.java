package mops.businesslogic;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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
public class FileServiceImpl implements FileService {

    /**
     * Service for permission checks.
     */
    private final transient DirectoryService directoryService;
    /**
     * Service for saving and retrieving file meta data.
     */
    private final transient FileInfoService fileInfoService;
    /**
     * File content repository.
     */
    private final transient FileRepository fileRepository;

    /**
     * Constructor.
     *
     * @param directoryService Service for permission checks.
     * @param fileInfoService  Service for saving and retrieving file meta data.
     * @param fileRepository   File content repository.
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
    @SuppressWarnings("PMD.LawOfDemeter")
    @Transactional(rollbackFor = MopsException.class)
    public void saveFile(Account account, long dirId, MultipartFile multipartFile,
                         Set<String> tags) throws MopsException {

        UserPermission userPermission = directoryService.getPermissionsOfUser(account, dirId);

        if (!userPermission.isWrite()) {
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
            throw new MopsException("Error while saving", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({"PMD.LawOfDemeter", "PMD.DataflowAnomalyAnalysis"})
    @SuppressFBWarnings(value = "RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE",
            justification = "There's no nullcheck here.")
    public FileContainer getFile(Account account, long fileId) throws MopsException {

        FileInfo fileInfo;
        try {
            fileInfo = fileInfoService.fetchFileInfo(fileId);
        } catch (MopsException e) {
            throw new FileNotFoundException("File not found", e);
        }

        UserPermission userPermission = directoryService.getPermissionsOfUser(account, fileInfo.getDirectoryId());

        if (!userPermission.isRead()) {
            throw new ReadAccessPermissionException("No read permission");
        }

        try (InputStream stream = fileRepository.getFileContent(fileId)) {
            ByteArrayResource byteArrayResource = new ByteArrayResource(stream.readAllBytes());
            return new FileContainer(fileInfo, byteArrayResource);
        } catch (MopsException | IOException e) {
            throw new MopsException("Error while retrieving", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({"PMD.LawOfDemeter", "PMD.DataflowAnomalyAnalysis"})
    @Transactional(rollbackFor = MopsException.class)
    public Directory deleteFile(Account account, long fileId) throws MopsException {
        FileInfo fileInfo;
        try {
            fileInfo = fileInfoService.fetchFileInfo(fileId);
        } catch (MopsException e) {
            throw new FileNotFoundException("File not found", e);
        }

        UserPermission userPermission = directoryService.getPermissionsOfUser(account, fileInfo.getDirectoryId());
        String owner = fileInfo.getOwner();
        boolean isOwner = owner.equals(account.getName());
        if (!isOwner && !userPermission.isDelete()) {
            throw new DeleteAccessPermissionException("No delete permission");
        }

        try {
            fileInfoService.deleteFileInfo(fileId);
            fileRepository.deleteFile(fileId);
        } catch (MopsException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new MopsException("Error while deleting", e);
        }
        return directoryService.getDirectory(fileInfo.getDirectoryId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({"PMD.LawOfDemeter", "PMD.DataflowAnomalyAnalysis"})
    public FileInfo getFileInfo(Account account, long fileId) throws MopsException {
        FileInfo fileInfo;
        try {
            fileInfo = fileInfoService.fetchFileInfo(fileId);
        } catch (MopsException e) {
            throw new FileNotFoundException("File not found", e);
        }
        UserPermission userPermission = directoryService.getPermissionsOfUser(account, fileInfo.getDirectoryId());

        if (!userPermission.isRead()) {
            throw new ReadAccessPermissionException("No read permission");
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
            throw new ReadAccessPermissionException("No read permission");
        }
        return fileInfoService.fetchAllFilesInDirectory(dirId);
    }
}
