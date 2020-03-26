package mops.businesslogic.directory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mops.businesslogic.exception.DatabaseException;
import mops.businesslogic.file.FileInfoService;
import mops.businesslogic.file.FileService;
import mops.businesslogic.permission.PermissionService;
import mops.businesslogic.security.Account;
import mops.businesslogic.security.SecurityService;
import mops.exception.MopsException;
import mops.persistence.directory.Directory;
import mops.persistence.file.FileInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;


/**
 * Handles directory and file deletion.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteServiceImpl implements DeleteService {

    /**
     * Handles communication with the storage service.
     */
    private final FileService fileService;
    /**
     * Handles meta data of files.
     */
    private final FileInfoService fileInfoService;
    /**
     * Handles meta data for directories.
     */
    private final DirectoryService directoryService;
    /**
     * Handles permissions for directories.
     */
    private final PermissionService permissionService;
    /**
     * Checks permissions.
     */
    private final SecurityService securityService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Directory deleteFolder(Account account, long dirId) throws MopsException {
        log.debug("Requesting deletion of folder with id '{}'.", dirId);
        Directory directory = directoryService.getDirectory(dirId);

        securityService.checkDeletePermission(account, directory);

        List<FileInfo> files = fileInfoService.fetchAllFilesInDirectory(dirId);
        List<Directory> subFolders = directoryService.getSubFolders(account, dirId);

        Directory parentDirectory = null;
        if (directory.getParentId() != null) {
            parentDirectory = directoryService.getDirectory(directory.getParentId());
        }

        try {
            for (FileInfo fileInfo : files) {
                log.debug("Deleting file '{}'.", fileInfo.getName());
                fileService.deleteFile(account, fileInfo.getId());
            }
            for (Directory subFolder : subFolders) {
                log.debug("Recursively deleting directory '{}'.", subFolder.getName());
                deleteFolder(account, subFolder.getId());
            }

            log.debug("Deleting directory '{}'.", directory.getName());
            directoryService.deleteDirectory(directory);

            if (parentDirectory == null || parentDirectory.getPermissionsId() != directory.getPermissionsId()) {
                log.debug("Deleting directory permissions with id '{}'.", directory.getPermissionsId());
                permissionService.deletePermissions(directory);
            }
        } catch (MopsException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error("Error while deleting directory {} by user {}:", directory.getName(), account.getName(), e);
            throw new DatabaseException("Fehler während des Löschens aufgetreten", e);
        }

        return parentDirectory;
    }
}
