package mops.businesslogic.prometheus;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import mops.businesslogic.DirectoryService;
import mops.businesslogic.FileInfoService;
import mops.businesslogic.Group;
import mops.businesslogic.GroupService;
import mops.exception.MopsException;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class PrometheusComponent {

    /**
     * Group service.
     */
    private final transient GroupService groupService;
    /**
     * File info service.
     */
    private final transient FileInfoService fileInfoService;
    /**
     * Directory service.
     */
    private final transient DirectoryService directoryService;
    /**
     * Meter registry.
     */
    private final transient MeterRegistry meterRegistry;

    /**
     * Constructor.
     *
     * @param groupService     group service
     * @param fileInfoService  file info service
     * @param directoryService directory service
     * @param meterRegistry    meter registry
     */
    public PrometheusComponent(GroupService groupService, FileInfoService fileInfoService,
                               DirectoryService directoryService, MeterRegistry meterRegistry) {
        this.groupService = groupService;
        this.fileInfoService = fileInfoService;
        this.directoryService = directoryService;
        this.meterRegistry = meterRegistry;

        addTotalGauges();
        addGroupGauges();
    }

    @SuppressWarnings("PMD.LawOfDemeter") //This is a builder
    private void addTotalGauges() {
        log.debug("Adding total storage gauge.");
        Gauge
                .builder("mops.material1.totalStorageUsage", () -> {
                            try {
                                return fileInfoService.getStorageUsage();
                            } catch (MopsException ignored) {
                                return 0L;
                            }
                        }
                )
                .register(meterRegistry);

        log.debug("Adding total file count gauge.");
        Gauge
                .builder("mops.material1.totalFileCount", () -> {
                            try {
                                return fileInfoService.getFileCount();
                            } catch (MopsException ignored) {
                                return 0L;
                            }
                        }
                )
                .register(meterRegistry);

        log.debug("Adding total directory count gauge.");
        Gauge
                .builder("mops.material1.totalDirCount", () -> {
                            try {
                                return directoryService.getDirCount();
                            } catch (MopsException ignored) {
                                return 0L;
                            }
                        }
                )
                .register(meterRegistry);
    }

    @SuppressWarnings({ "PMD.LawOfDemeter", "PMD.DataflowAnomalyAnalysis" }) //This is a builder
    private void addGroupGauges() {
        List<Group> groups = List.of();
        try {
            groups = groupService.getAllGroups();
        } catch (MopsException ignored) {
        }

        groups.forEach(group -> {
            log.debug("Adding group storage gauge for group {}.", group);
            Gauge
                    .builder("mops.material1.groupStorageUsage", () -> {
                                try {
                                    return fileInfoService.getStorageUsage(group.getId());
                                } catch (MopsException ignored) {
                                    return 0L;
                                }
                            }
                    )
                    .tag("group_id", String.valueOf(group.getId()))
                    .register(meterRegistry);
        });

        groups.forEach(group -> {
            log.debug("Adding group file count gauge for group {}.", group);
            Gauge
                    .builder("mops.material1.groupFileCount", () -> {
                                try {
                                    return fileInfoService.getFileCount(group.getId());
                                } catch (MopsException ignored) {
                                    return 0L;
                                }
                            }
                    )
                    .tag("group_id", String.valueOf(group.getId()))
                    .register(meterRegistry);
        });

        groups.forEach(group -> {
            log.debug("Adding group dir count gauge for group {}.", group);
            Gauge
                    .builder("mops.material1.groupDirCount", () -> {
                                try {
                                    return directoryService.getDirCount(group.getId());
                                } catch (MopsException ignored) {
                                    return 0L;
                                }
                            }
                    )
                    .tag("group_id", String.valueOf(group.getId()))
                    .register(meterRegistry);
        });
    }
}
