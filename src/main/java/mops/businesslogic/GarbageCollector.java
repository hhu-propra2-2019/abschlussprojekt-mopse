package mops.businesslogic;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mops.businesslogic.file.FileInfoService;
import mops.businesslogic.file.FileServiceImpl;
import mops.exception.MopsException;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Removes content of no longer existing groups.
 */
@Profile("!test")
@Component
@EnableScheduling
@Slf4j
@AllArgsConstructor
public class GarbageCollector {

    /**
     * One week in milliseconds.
     */
    private static final long ONE_WEEK = 7L * 24L * 60L * 60L * 1000L;

    /**
     * FileInfoService.
     */
    private final FileInfoService fileInfoService;
    /**
     * FileService.
     */
    private final FileServiceImpl fileService;

    /**
     * Collects all IDs, find orphans and delete them.
     */
    @Scheduled(fixedDelay = ONE_WEEK)
    @SuppressWarnings({ "PMD.DataflowAnomalyAnalysis", "PMD.OnlyOneReturn" })
    public void removeOrphans() {
        log.info("Starting garbage collection.");

        Set<Long> metaIds;
        Set<Long> fileIds;
        try {
            metaIds = new HashSet<>(fileInfoService.fetchAllFileInfoIds());
            fileIds = new HashSet<>(fileService.getAllFileIds());
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
                fileService.deleteFile(fileId);
                log.debug("Removed orphaned File with ID {}", fileId);

            }
        } catch (MopsException e) {
            log.error("There was an error while removing orphans:", e);
        }
    }
}
