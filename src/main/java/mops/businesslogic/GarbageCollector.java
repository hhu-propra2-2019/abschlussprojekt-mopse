package mops.businesslogic;

import lombok.AllArgsConstructor;
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
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Removes content of no longer existing groups.
 */
@Profile("!test")
@Component
@Slf4j
@AllArgsConstructor
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
     * Garbage Collector Account
     */
    private Account gbAccount;

    /**
     * Starts garbage collection.
     */
    @Scheduled(fixedDelay = ONE_DAY)
    public void garbageCollection() {
        log.info("Starting garbage collection.");
        gbAccount = Account.of(); // TODO
        removeOrphanedFiles();
        removeOrphanedDirs();
        log.info("Garbage collection finished.");
    }

    /**
     * Collects all IDs, find orphans and delete them.
     */
    @SuppressWarnings({ "PMD.DataflowAnomalyAnalysis", "PMD.OnlyOneReturn" })
    public void removeOrphanedFiles() {
        Set<Long> metaIds;
        Set<Long> fileIds;
        Set<Long> filesWithoutDirectory;
        try {
            metaIds = new HashSet<>(fileInfoService.fetchAllFileInfoIds());
            fileIds = new HashSet<>(fileService.getAllFileIds());
            filesWithoutDirectory = new HashSet<>(fileInfoService.fetchAllOrphanedFileInfos());
        } catch (MopsException e) {
            log.error("There was an error while collecting all IDs:", e);
            return;
        }

        Set<Long> intersection = new HashSet<>(metaIds);
        boolean changed = intersection.retainAll(fileIds);

        if (!changed) {
            // no orphans found
            return;
        }

        // only orphans left
        metaIds.removeAll(intersection);
        fileIds.removeAll(intersection);

        long count = metaIds.size() + fileIds.size();
        log.info("{} orphans were found. {} FileInfo Entries and {} Files.",
                count,
                metaIds.size(),
                fileIds.size()
        );

        try {
            for (Long metaId : metaIds) {
                fileInfoService.deleteFileInfo(metaId);
                log.debug("Removed FileInfo orphan with ID {}", metaId);
            }

            for (Long fileId : fileIds) {
                fileService.deleteFileWithoutMeta(fileId);
                log.debug("Removed orphaned File with ID {}", fileId);

            }

            for (Long fileId : filesWithoutDirectory) {
                fileService.deleteFile(gbAccount, fileId);
                log.debug("Removed orphaned File with ID {} from non existing directory", fileId);
            }
        } catch (MopsException e) {
            log.error("There was an error while removing orphans:", e);
        }
    }


    /**
     * Looks for all groups in our database and existing directories.
     * Removes all directories without an existing group.
     */
    @SuppressWarnings({ "PMD.DataflowAnomalyAnalysis", "PMD.LawOfDemeter" })
    public void removeOrphanedDirs() {
        Set<Long> groupIdsFromGroups = new HashSet<>();
        Set<Long> groupIdsFromDirectories = new HashSet<>();

        try {
            List<Group> allGroups = groupService.getAllGroups();
            List<Directory> allRootDirectories = directoryService.getAllRootDirectories();
            allGroups.forEach(g -> groupIdsFromGroups.add(g.getId()));
            allRootDirectories.forEach(r -> groupIdsFromDirectories.add(r.getGroupOwner()));
        } catch (MopsException e) {
            log.error("Error on retrieving all groups", e);
            return;
        }

        Set<Long> intersection = new HashSet<>(groupIdsFromGroups);
        intersection.retainAll(groupIdsFromDirectories);
        // only orphans left
        groupIdsFromDirectories.removeAll(intersection);

        groupIdsFromDirectories.forEach(group -> {
            try {
                deleteService.deleteFolder(gbAccount, group);
            } catch (MopsException e) {
                log.error("Couldn't remove orphaned root dir with id {}", group, e);
            }
        });
    }
}
