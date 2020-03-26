package mops.businesslogic.gruppenfindung;

import lombok.RequiredArgsConstructor;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Component that updates our group database.
 */
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
     * Connection to the Gruppenfindung REST API.
     */
    private final GruppenfindungsService gruppenfindungsService;
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
    public void updateDatabase() throws MopsException {
        LatestEventId latestEventId = latestEventIdService.getLatestEventId();

        UpdatedGroupsDTO updatedGroups = gruppenfindungsService.getUpdatedGroups(latestEventId.getEventId());

        List<Group> updated = new ArrayList<>();
        List<Long> deleted = new ArrayList<>();

        for (GroupDTO groupDAO : updatedGroups.getGroupDAOs()) {
            UUID groupId = groupDAO.getGroupId();
            Optional<Group> optionalGroup = groupService.findGroupByGroupId(groupId);

            switch (groupDAO.getStatus()) {
                case ACTIVE:
                    GroupBuilder builder = Group.builder()
                            .groupId(groupId)
                            .name(groupDAO.getGroupName());

                    optionalGroup.ifPresent(g -> builder.id(g.getId()));

                    for (UserDTO userDTO : gruppenfindungsService.getMembers(groupDAO.getGroupId())) {
                        String name = userDTO.getUsername();
                        boolean admin = gruppenfindungsService.isUserAdminInGroup(name, groupId);
                        String role = admin ? adminRole : viewerRole;
                        builder.member(name, role);
                    }
                    updated.add(builder.build());
                    break;
                case DEACTIVATED:
                    optionalGroup.ifPresent(g -> deleted.add(g.getId()));
                    break;
                default: // switch is exhaustive, this should never happen
                    throw new ImpossibleException("Unerwarteter Fehler - dies sollte nicht passieren");
            }
        }

        groupService.saveAllGroups(updated);
        groupService.deleteAllGroups(deleted);

        latestEventId.setEventId(updatedGroups.getEventId());
        latestEventIdService.saveLatestEventId(latestEventId);
    }
}
