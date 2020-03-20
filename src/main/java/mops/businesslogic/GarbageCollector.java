package mops.businesslogic;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mops.exception.MopsException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@EnableScheduling
@Slf4j
@AllArgsConstructor
public class GarbageCollector {

    /**
     * One week in milliseconds.
     */
    private static final long ONE_WEEK = 604_800_016;

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
    void removeOrphans() {
        Set<Long> metaIds;
        Set<Long> fileIds;
        try {
            metaIds = new HashSet<>(fileInfoService.fetchAllFileInfoIds());
            fileIds = new HashSet<>(fileService.getAllFileIds());
        } catch (MopsException e) {
            log.error("There was an error while collecting all IDs: {}", e.getMessage());
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

        try {
            for (Long metaId : metaIds) {
                fileInfoService.deleteFileInfo(metaId);
            }

            for (Long fileId : fileIds) {
                fileService.deleteFile(fileId);
            }
        } catch (MopsException e) {
            log.error("There was an error while removing orphans: {}", e.getMessage());
        }
    }
}
