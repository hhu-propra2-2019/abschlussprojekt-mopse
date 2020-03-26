package mops.businesslogic.delete;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mops.businesslogic.directory.DirectoryService;
import mops.businesslogic.file.FileInfoService;
import mops.businesslogic.file.FileService;
import mops.businesslogic.permission.PermissionService;
import mops.businesslogic.security.Account;
import mops.exception.MopsException;
import mops.persistence.directory.Directory;
import mops.persistence.file.FileInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;


/**
 * Handles directory and file deletion.
 */
@Service
@RequiredArgsConstructor
@Slf4j
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
     * {@inheritDoc}
     */
    @Override
    public Directory deleteFolder(Account account, long dirId) throws MopsException {
        Directory directory = directoryService.getDirectory(dirId);

        List<FileInfo> files = fileInfoService.fetchAllFilesInDirectory(dirId);
        List<Directory> subFolders = directoryService.getSubFolders(account, dirId);

        for (FileInfo fileInfo : files) {
            fileService.deleteFile(account, fileInfo.getId());
        }
        for (Directory dir : subFolders) {
            deleteFolder(account, dir.getId());
        }

        Directory parentDirectory = null;
        if (directory.getParentId() != null) {
            parentDirectory = directoryService.getDirectory(directory.getParentId());
        }

        try {
            if (parentDirectory == null || parentDirectory.getPermissionsId() != directory.getPermissionsId()) {
                permissionService.deletePermissions(directory);
            }
        } catch (MopsException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error("Error while deleting directory {} by user {}:", directory.getName(), account.getName(), e);
            throw new MopsException("Fehler während des Löschens aufgetreten", e);
        }

        return parentDirectory;
    }
}
