package mops.businesslogic.gruppenbildung;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mops.businesslogic.event.LatestEventIdService;
import mops.businesslogic.group.GroupService;
import mops.exception.ImpossibleException;
import mops.exception.MopsException;
import mops.persistence.event.LatestEventId;
import mops.persistence.group.Group;
import mops.persistence.group.GroupBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Component that updates our group database.
 */
@Slf4j
@Component
@Profile("prod")
@RequiredArgsConstructor
public class GroupUpdater {

    /**
     * Update rate of database. Currently one minute.
     */
    private static final long UPDATE_RATE = 60L * 1000L;

    /**
     * Represents the role of an admin.
     */
    @Value("${material1.mops.configuration.role.admin}")
    @SuppressWarnings({ "PMD.ImmutableField", "PMD.BeanMembersShouldSerialize" })
    private String adminRole = "admin";
    /**
     * Represents the role of a viewer.
     */
    @Value("${material1.mops.configuration.role.viewer}")
    @SuppressWarnings({ "PMD.ImmutableField", "PMD.BeanMembersShouldSerialize" })
    private String viewerRole = "viewer";

    /**
     * Connection to the Gruppenbildung REST API.
     */
    private final GruppenbildungsService gruppenbildungsService;
    /**
     * Connection to the saved latest event id.
     */
    private final LatestEventIdService latestEventIdService;
    /**
     * Connection to our group database.
     */
    private final GroupService groupService;

    /**
     * Scheduled function to update our database the external group event stream.
     */
    @Scheduled(fixedRate = UPDATE_RATE)
    // optional, builder
    @SuppressWarnings({ "PMD.CognitiveComplexity", "PMD.LawOfDemeter", "PMD.DataflowAnomalyAnalysis" })
    public void updateDatabase() throws MopsException {
        log.debug("Pulling group database update from Gruppenbildung.");
        LatestEventId latestEventId = latestEventIdService.getLatestEventId();
        log.debug("Current latest event id is '{}'.", latestEventId.getEventId());

        UpdatedGroupsDTO updatedGroups = gruppenbildungsService.getUpdatedGroups(latestEventId.getEventId());

        int added = 0;
        int changed = 0;
        int deleted = 0;

        for (GroupDTO groupDAO : updatedGroups.getGroupDAOs()) {
            UUID groupId = groupDAO.getGroupId();
            Optional<Group> optionalGroup = groupService.findGroupByGroupId(groupId);

            switch (groupDAO.getStatus()) {
                case ACTIVE:
                    GroupBuilder builder = Group.builder()
                            .groupId(groupId)
                            .name(groupDAO.getGroupName());

                    if (optionalGroup.isPresent()) {
                        long id = optionalGroup.get().getId();
                        builder.id(id);
                        changed++;
                    } else {
                        added++;
                    }

                    for (UserDTO userDTO : gruppenbildungsService.getMembers(groupDAO.getGroupId())) {
                        String name = userDTO.getUserName();
                        boolean admin = gruppenbildungsService.isUserAdminInGroup(name, groupId);
                        String role = admin ? adminRole : viewerRole;
                        builder.member(name, role);
                    }
                    groupService.saveGroup(builder.build());
                    break;
                case DEACTIVATED:
                    if (optionalGroup.isPresent()) {
                        long id = optionalGroup.get().getId();
                        groupService.deleteGroup(id);
                        deleted++;
                    }
                    break;
                default: // switch is exhaustive, this should never happen
                    throw new ImpossibleException("Unerwarteter Fehler - dies sollte nicht passieren");
            }
        }

        log.debug("{} groups added.", added);
        log.debug("{} groups changed.", changed);
        log.debug("{} groups deleted.", deleted);
        log.debug("New latest event id is '{}'.", updatedGroups.getEventId());

        latestEventId.setEventId(updatedGroups.getEventId());
        latestEventIdService.saveLatestEventId(latestEventId);
    }
}
