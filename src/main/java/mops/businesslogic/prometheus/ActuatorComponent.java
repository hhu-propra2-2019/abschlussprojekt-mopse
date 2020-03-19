package mops.businesslogic.prometheus;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import mops.businesslogic.FileInfoService;
import mops.businesslogic.Group;
import mops.businesslogic.GroupService;
import mops.exception.MopsException;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ActuatorComponent {

    /**
     * Group service.
     */
    private final GroupService groupService;
    /**
     * File info service.
     */
    private final FileInfoService fileInfoService;
    /**
     * Meter registry.
     */
    private final MeterRegistry meterRegistry;

    /**
     * Constructor.
     *
     * @param groupService    group service
     * @param fileInfoService file info service
     * @param meterRegistry   meter registry
     */
    public ActuatorComponent(GroupService groupService, FileInfoService fileInfoService, MeterRegistry meterRegistry) {
        this.groupService = groupService;
        this.fileInfoService = fileInfoService;
        this.meterRegistry = meterRegistry;

        addGroupStorageGauges();
    }

    private void addGroupStorageGauges() {
        log.debug("addGroupStorageGauges()");
        List<Group> groups = List.of();
        try {
            groups = groupService.getAllGroups();
        } catch (MopsException ignored) {
        }

        groups.forEach(group -> {
            log.debug("Adding group storage gauge for group {}.", group);
            Gauge
                    .builder("mops.material1.groupStorage", () -> {
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
    }

    private void addGroupFileCountGauges() {
        log.debug("addGroupFileCountGauges()");
        List<Group> groups = List.of();
        try {
            groups = groupService.getAllGroups();
        } catch (MopsException ignored) {
        }

        groups.forEach(group -> {
            log.debug("Adding group storage gauge for group {}.", group);
            Gauge
                    .builder("mops.material1.groupStorage", () -> {
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
    }
}
