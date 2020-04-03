package mops.businesslogic;

import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mops.businesslogic.directory.DeleteService;
import mops.businesslogic.directory.DirectoryService;
import mops.businesslogic.file.FileInfoService;
import mops.businesslogic.file.FileService;
import mops.businesslogic.group.GroupService;
import mops.businesslogic.security.Account;
import mops.exception.MopsException;
import mops.persistence.directory.Directory;
import mops.persistence.group.Group;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Removes content of no longer existing groups.
 */
@Profile("!test")
@Component
@Slf4j
@RequiredArgsConstructor
public class GarbageCollector {

    /**
     * One week in milliseconds.
     */
    private static final long ONE_DAY = 24L * 60L * 60L * 1000L;

    /**
     * FileInfoService.
     */
    private final FileInfoService fileInfoService;
    /**
     * FileService.
     */
    private final FileService fileService;
    /**
     * DeleteService.
     */
    private final DeleteService deleteService;
    /**
     * GroupService.
     */
    private final GroupService groupService;
    /**
     * DirectoryService.
     */
    private final DirectoryService directoryService;

    /**
     * Garbage Collector Account.
     */
    private Account gbAccount;

    /**
     * Name of internal admin role.
     */
    @Value("${material1.mops.configuration.role.internal-admin}")
    @SuppressWarnings({ "PMD.ImmutableField", "PMD.BeanMembersShouldSerialize" })
    private String internalAdminRole = "material1_internal_admin";

    /**
     * Starts garbage collection.
     */
    @Scheduled(fixedDelay = ONE_DAY)
    public void garbageCollection() {
        log.info("Starting garbage collection.");
        removeOrphanedFiles();
        removeOrphanedDirs();
        log.info("Garbage collection finished.");
    }

    /**
     * Collects all IDs, finds orphaned files and deletes them.
     */
    @SuppressWarnings({ "PMD.DataflowAnomalyAnalysis", "PMD.LawOfDemeter", "PMD.DefaultPackage" })
    void removeOrphanedFiles() {
        initAccount();

        Set<Long> metaIds;
        Set<Long> fileIds;
        Set<Long> filesWithoutDirectory;
        try {
            metaIds = new HashSet<>(fileInfoService.fetchAllFileInfoIds());
            fileIds = new HashSet<>(fileService.getAllFileIds());
            filesWithoutDirectory = new HashSet<>(fileInfoService.fetchAllOrphanedFileInfos());
        } catch (MopsException e) {
            log.error("Error while collecting all file ids:", e);
            return;
        }

        Set<Long> filesWithMeta = Sets.intersection(metaIds, fileIds).immutableCopy();

        // only orphans left
        metaIds.removeAll(filesWithMeta);
        fileIds.removeAll(filesWithMeta);

        int count = metaIds.size() + fileIds.size();
        log.info("{} orphaned files were found. {} FileInfos and {} Files.",
                count,
                metaIds.size(),
                fileIds.size()
        );

        try {
            for (Long metaId : metaIds) {
                fileInfoService.deleteFileInfo(metaId);
                log.debug("Removed FileInfo orphan with id '{}'.", metaId);
            }

            for (Long fileId : fileIds) {
                fileService.deleteFileWithoutMeta(fileId);
                log.debug("Removed orphaned File with id '{}'.", fileId);

            }

            for (Long fileId : filesWithoutDirectory) {
                fileService.deleteFile(gbAccount, fileId);
                log.debug("Removed orphaned File with id '{}' from non existing directory.", fileId);
            }
        } catch (MopsException e) {
            log.error("There was an error while removing orphaned files:", e);
        }
    }

    /**
     * Looks for all groups in our database and existing directories.
     * Removes all directories without an existing group.
     */
    @SuppressWarnings({ "PMD.DataflowAnomalyAnalysis", "PMD.LawOfDemeter", "PMD.DefaultPackage" })
    void removeOrphanedDirs() {
        initAccount();

        Set<Long> groupIds;
        Map<Long, Long> groupRootDirs;

        try {
            groupIds = groupService.getAllGroups().stream()
                    .map(Group::getId)
                    .collect(Collectors.toSet());

            groupRootDirs = directoryService.getAllRootDirectories().stream()
                    .collect(Collectors.toMap(Directory::getGroupOwner, Directory::getId));
        } catch (MopsException e) {
            log.error("Error on retrieving all groups", e);
            return;
        }

        Set<Long> pairedDirectories = Sets.intersection(groupIds, groupRootDirs.keySet()).immutableCopy();
        // only orphans left
        groupRootDirs.keySet().removeAll(pairedDirectories);

        log.info("{} orphaned root directories were found.", groupRootDirs.size());

        groupRootDirs.forEach((groupId, rootDirId) -> {
            try {
                deleteService.deleteFolder(gbAccount, rootDirId);
                log.debug("Removed orphaned root directory of group {}with id {}.", groupId, rootDirId);
            } catch (MopsException e) {
                log.error("Couldn't remove orphaned root dir of group {} with id {}.", groupId, rootDirId, e);
            }
        });
    }

    private void initAccount() {
        if (gbAccount == null) {
            gbAccount = Account.of("GarbageCollector", "mops.hhu.de", internalAdminRole);
        }
    }
}
