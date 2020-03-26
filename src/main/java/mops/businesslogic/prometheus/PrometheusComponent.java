package mops.businesslogic.prometheus;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import mops.businesslogic.directory.DirectoryService;
import mops.businesslogic.file.FileInfoService;
import mops.businesslogic.group.GroupService;
import mops.exception.MopsException;
import mops.persistence.group.Group;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Prometheus integration.
 */
@Slf4j
@Component
@Profile("!test")
public class PrometheusComponent {

    /**
     * One hour in milliseconds.
     */
    private static final long ONE_HOUR_IN_MS = 60L * 60L * 1000L;

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
     * Groups for which there were stats added already.
     */
    private final transient Set<Long> seenGroups = new HashSet<>();

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

        addGlobalGauges();
        addGroupGauges();
    }

    /**
     * Update all the gauges.
     */
    @Scheduled(fixedRate = ONE_HOUR_IN_MS)
    public void updateGauges() {
        addGroupGauges();
    }

    @SuppressWarnings("PMD.LawOfDemeter")
    private void addGlobalGauges() {
        log.debug("Adding new global gauges.");

        addGlobalGauge("totalStorageUsage", "total storage usage", fileInfoService::getTotalStorageUsage);
        addGlobalGauge("totalFileCount", "total file count", fileInfoService::getTotalFileCount);
        addGlobalGauge("totalDirCount", "total directory count", directoryService::getTotalDirCount);
    }

    @SuppressWarnings({ "PMD.LawOfDemeter", "PMD.DataflowAnomalyAnalysis" })
    private void addGroupGauges() {
        log.debug("Adding new group gauges.");

        Set<Group> groups = new HashSet<>();
        try {
            groups.addAll(groupService.getAllGroups());
        } catch (MopsException e) {
            log.error("Error while getting all groups from gauge setup:", e);
        }

        groups.forEach(this::addGroupGauge);
    }

    @SuppressWarnings("PMD.LawOfDemeter")
    private void addGroupGauge(Group group) {
        if (seenGroups.add(group.getId())) {
            log.debug("Adding new gauges for group '{}' (id {}).", group.getName(), group.getId());

            addGroupGauge("groupStorageUsage", "storage usage", group,
                    fileInfoService::getStorageUsageInGroup);
            addGroupGauge("groupFileCount", "file count", group,
                    fileInfoService::getFileCountInGroup);
            addGroupGauge("groupDirCount", "directory count", group,
                    directoryService::getDirCountInGroup);
        } else {
            log.debug("Gauges for group '{}' already exist, skipping.", group);
        }
    }

    @SuppressWarnings("PMD.LawOfDemeter") //This is a builder
    private void addGlobalGauge(String key, String message, GlobalStatSupplier statGetter) {
        log.debug("Adding '{}' gauge.", message);
        Gauge
                .builder("mops.material1." + key, () -> {
                            try {
                                return statGetter.getGlobalStat();
                            } catch (MopsException e) {
                                log.error("Error while reading '{}' from gauge:", message, e);
                                return 0L;
                            }
                        }
                )
                .register(meterRegistry);
    }

    @SuppressWarnings("PMD.LawOfDemeter") //This is a builder
    private void addGroupGauge(String key, String message, Group group, GroupStatSupplier statGetter) {
        log.debug("Adding '{}' gauge for group {} (id {}).", message, group.getName(), group.getId());
        Gauge
                .builder("mops.material1." + key, () -> {
                    try {
                        return statGetter.getGroupStat(group.getId());
                    } catch (MopsException e) {
                        log.error("Error while reading '{}' in group '{}' (id {}) from gauge:",
                                message, group.getName(), group.getId(), e);
                        return 0L;
                    }
                })
                .tag("group_id", String.valueOf(group.getId()))
                .register(meterRegistry);
    }

    /**
     * Private Functional Interface to get a global statistic.
     */
    @FunctionalInterface
    private interface GlobalStatSupplier {

        /**
         * Gets global statistic.
         *
         * @return stat
         * @throws MopsException on error
         */
        Number getGlobalStat() throws MopsException;

    }

    /**
     * Private Functional Interface to get a group statistic.
     */
    @FunctionalInterface
    private interface GroupStatSupplier {

        /**
         * Gets the group's statistic.
         *
         * @param groupId group
         * @return stat
         * @throws MopsException on error
         */
        Number getGroupStat(long groupId) throws MopsException;

    }
}
