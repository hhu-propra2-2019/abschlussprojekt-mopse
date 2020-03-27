package mops.businesslogic.prometheus;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.BaseUnits;
import lombok.extern.slf4j.Slf4j;
import mops.businesslogic.directory.DirectoryService;
import mops.businesslogic.exception.DatabaseException;
import mops.businesslogic.file.FileInfoService;
import mops.businesslogic.group.GroupService;
import mops.exception.MopsException;
import mops.persistence.group.Group;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    @SuppressWarnings("UnstableApiUsage")
    private final transient Multimap<Long, Meter.Id> groupGauges = MultimapBuilder.hashKeys().hashSetValues().build();

    /**
     * Constructor.
     *
     * @param groupService     group service
     * @param fileInfoService  file info service
     * @param directoryService directory service
     * @param meterRegistry    meter registry
     */
    public PrometheusComponent(GroupService groupService, FileInfoService fileInfoService,
                               DirectoryService directoryService, MeterRegistry meterRegistry) throws MopsException {
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
    public void updateGauges() throws MopsException {
        addGroupGauges();
    }

    @SuppressWarnings("PMD.LawOfDemeter")
    private void addGlobalGauges() {
        log.debug("Adding new global gauges.");

        addGlobalGauge("totalStorageUsage", "total storage usage", BaseUnits.BYTES,
                fileInfoService::getTotalStorageUsage);
        addGlobalGauge("totalFileCount", "total file count", BaseUnits.FILES,
                fileInfoService::getTotalFileCount);
        addGlobalGauge("totalDirCount", "total directory count", "directories",
                directoryService::getTotalDirCount);
        addGlobalGauge("totalGroupCount", "total group count", "groups",
                groupService::getTotalGroupCount);
    }

    @SuppressWarnings({ "PMD.LawOfDemeter", "PMD.DataflowAnomalyAnalysis" })
    private void addGroupGauges() throws MopsException {
        log.debug("Adding new group gauges.");

        List<Group> groups;
        try {
            groups = groupService.getAllGroups();
        } catch (MopsException e) {
            log.error("Error while getting all groups from gauge setup:", e);
            throw new DatabaseException("Fehler beim Laden aller Gruppen für Prometheus.", e);
        }

        groups.forEach(this::addGroupGauge);

        Set<Long> groupIds = groups.stream().map(Group::getId).collect(Collectors.toSet());
        Set<Long> removedGroups = groupGauges.keySet().stream()
                .filter(groupId -> !groupIds.contains(groupId))
                .collect(Collectors.toSet());

        removedGroups.forEach(id -> groupGauges.removeAll(id).forEach(meterRegistry::remove));
    }

    @SuppressWarnings("PMD.LawOfDemeter")
    private void addGroupGauge(Group group) {
        if (groupGauges.containsKey(group.getId())) {
            log.debug("Adding new gauges for group '{}' (id {}).", group.getName(), group.getId());

            addGroupGauge("groupStorageUsage", "storage usage", BaseUnits.BYTES, group,
                    fileInfoService::getStorageUsageInGroup);
            addGroupGauge("groupFileCount", "file count", BaseUnits.FILES, group,
                    fileInfoService::getFileCountInGroup);
            addGroupGauge("groupDirCount", "directory count", "directories", group,
                    directoryService::getDirCountInGroup);
        } else {
            log.debug("Gauges for group '{}' already exist, skipping.", group);
        }
    }

    @SuppressWarnings("PMD.LawOfDemeter") //This is a builder
    private void addGlobalGauge(String key, String message, String unit, GlobalStatSupplier statGetter) {
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
                .description(message)
                .baseUnit(unit)
                .register(meterRegistry);
    }

    @SuppressWarnings({ "PMD.LawOfDemeter", "PMD.DataflowAnomalyAnalysis" }) //This is a builder
    private void addGroupGauge(String key, String message, String unit, Group group, GroupStatSupplier statGetter) {
        log.debug("Adding '{}' gauge for group {} (id {}).", message, group.getName(), group.getId());
        Gauge gauge = Gauge
                .builder("mops.material1." + key, () -> {
                    try {
                        return statGetter.getGroupStat(group.getId());
                    } catch (MopsException e) {
                        log.error("Error while reading '{}' in group '{}' (id {}) from gauge:",
                                message, group.getName(), group.getId(), e);
                        return 0L;
                    }
                })
                .description("group " + message)
                .baseUnit(unit)
                .tag("group_id", String.valueOf(group.getGroupId()))
                .register(meterRegistry);

        groupGauges.put(group.getId(), gauge.getId());
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
