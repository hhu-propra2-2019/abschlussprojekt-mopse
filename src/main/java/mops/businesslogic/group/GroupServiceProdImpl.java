package mops.businesslogic.group;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mops.businesslogic.exception.DatabaseException;
import mops.businesslogic.exception.GruppenfindungsException;
import mops.businesslogic.security.Account;
import mops.exception.MopsException;
import mops.persistence.GroupRepository;
import mops.persistence.group.Group;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * {@inheritDoc}
 * This is used during production.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Profile("prod")
public class GroupServiceProdImpl implements GroupService {

    /**
     * Update rate of database. Currently one minute.
     */
    private static final long UPDATE_RATE = 60L * 1000L;

    /**
     * REST-API-URL of gruppen1.
     */
    @Value("${material1.mops.gruppenfindung.url}")
    private String gruppenfindungsUrl = "https://mops.hhu.de/gruppen1";
    /**
     * Represents the role of an admin.
     */
    @Value("${material1.mops.configuration.role.admin}")
    private String adminRole = "admin";
    /**
     * Represents the role of a viewer.
     */
    @Value("${material1.mops.configuration.role.viewer}")
    private String viewerRole = "viewer";

    /**
     * Access to our Group Database.
     */
    private final GroupRepository groupRepository;
    /**
     * Allows to send REST API calls.
     */
    private final RestTemplate restTemplate;

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public boolean doesGroupExist(long groupId) throws MopsException {
        log.debug("Request existence of group '{}'.", groupId);
        throw new UnsupportedOperationException("nyi");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.LawOfDemeter") // stream
    public Set<String> getRoles(long groupId) throws MopsException {
        log.debug("Request roles in group '{}'", groupId);
        if (doesGroupExist(groupId)) {
            return Set.of(adminRole, viewerRole);
        }

        return Set.of();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Group> getAllGroups() throws MopsException {
        log.debug("Request all groups.");
        throw new UnsupportedOperationException("nyi");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Group> getUserGroups(Account account) throws MopsException {
        log.debug("Request all groups of user '{}'.", account.getName());
        throw new UnsupportedOperationException("nyi");
    }

    @Transactional(rollbackFor = MopsException.class)
    @Scheduled(fixedRate = UPDATE_RATE)
    @SuppressWarnings("checkstyle:DesignForExtension")
    void updateDatabase() throws MopsException {
        // TODO: implement with timestamp and deltas
        /*try {
            deleteAllGroups();
            List<GroupDTO> groups = returnAllGroups();

            for (GroupDTO groupDto : groups) {
                List<UserDTO> userDtos = returnUsersOfGroup(groupDto.getId());
                Group.builder().name(groupDto.name)
            }
            returnUsersOfGroup(...);
            isUserAdminInGroup(...);

            groupRepository.saveAll(...);
        } catch (MopsException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error("Error while updating group database:", e);
        }*/
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "PMD.LawOfDemeter", "PMD.AvoidCatchingGenericException" })
    public Group getGroup(long groupId) throws MopsException {
        try {
            return groupRepository.findById(groupId).orElseThrow();
        } catch (Exception e) {
            log.error("Failed to retrieve group with id '{}':", groupId, e);
            throw new DatabaseException(
                    "Die Gruppe konnte nicht gefunden werden, bitte versuchen sie es später nochmal!", e);
        }
    }

    @SuppressWarnings({ "PMD.LawOfDemeter", "PMD.AvoidCatchingGenericException" })
    private Group saveGroup(Group group) throws MopsException {
        try {
            return groupRepository.save(group);
        } catch (Exception e) {
            log.error("Failed to save group with name '{}' (id {}):", group.getName(), group.getId(), e);
            throw new DatabaseException("Die Gruppe konnte nicht gespeichert werden!", e);
        }
    }

    @SuppressWarnings({ "PMD.LawOfDemeter", "PMD.AvoidCatchingGenericException" })
    private List<Group> saveAllGroups(List<Group> groups) throws MopsException {
        try {
            List<Group> saved = new ArrayList<>();
            groupRepository.saveAll(groups).forEach(saved::add);
            return saved;
        } catch (Exception e) {
            log.error("Failed to save {} groups:", groups.size(), e);
            throw new DatabaseException("Die Gruppen konnte nicht gespeichert werden!", e);
        }
    }

    @SuppressWarnings({ "PMD.LawOfDemeter", "PMD.AvoidCatchingGenericException" })
    private void deleteAllGroups() throws MopsException {
        try {
            groupRepository.deleteAll();
        } catch (Exception e) {
            log.error("Failed to delete all groups:", e);
            throw new DatabaseException("Alle Gruppen konnte nicht gelöscht werden!", e);
        }
    }

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    private boolean doesGroupExistExternal(long groupId) throws MopsException {
        try {
            Boolean result = restTemplate.getForObject(gruppenfindungsUrl + "/isUserAdminInGroup?groupId={groupId}",
                    Boolean.class,
                    String.valueOf(groupId)
            );
            return Objects.requireNonNull(result, "got null response from GET");
        } catch (Exception e) {
            throw new GruppenfindungsException("...", e);
        }
    }

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    private boolean isUserInGroup(String userName, long groupId) throws MopsException {
        try {
            Boolean result = restTemplate.getForObject(
                    gruppenfindungsUrl + "/isUserInGroup?userName={userName}&groupId={groupId}",
                    Boolean.class,
                    userName,
                    String.valueOf(groupId)
            );
            return Objects.requireNonNull(result, "got null response from GET");
        } catch (Exception e) {
            throw new GruppenfindungsException("...", e);
        }
    }

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    private boolean isUserAdminInGroup(String userName, long groupId) throws MopsException {
        try {
            Boolean result = restTemplate.getForObject(
                    gruppenfindungsUrl + "/isUserAdminInGroup?userName={userName}&groupId={groupId}",
                    Boolean.class,
                    userName,
                    String.valueOf(groupId)
            );
            return Objects.requireNonNull(result, "got null response from GET");
        } catch (Exception e) {
            throw new GruppenfindungsException("...", e);
        }
    }

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    private List<GroupDTO> returnAllGroups() throws MopsException {
        try {
            return restTemplate.exchange(
                    gruppenfindungsUrl + "/returnAllGroups",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<GroupDTO>>() {
                    }
            ).getBody();
        } catch (Exception e) {
            throw new GruppenfindungsException("...", e);
        }
    }

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    private List<UserDTO> returnUsersOfGroup(long groupId) throws MopsException {
        try {
            return restTemplate.exchange(
                    gruppenfindungsUrl + "/returnUsersOfGroup?groupId={groupId}",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<UserDTO>>() {
                    },
                    String.valueOf(groupId)
            ).getBody();
        } catch (Exception e) {
            throw new GruppenfindungsException("...", e);
        }
    }

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    private List<GroupDTO> returnGroupsOfUsers(String userName) throws MopsException {
        try {
            return restTemplate.exchange(
                    gruppenfindungsUrl + "/returnGroupsOfUsers?userName={userName}",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<GroupDTO>>() {
                    },
                    userName
            ).getBody();
        } catch (Exception e) {
            throw new GruppenfindungsException("...", e);
        }
    }

    static class GroupDTO {

        // TODO: fields, once gruppen1 adds them

    }

    static class UserDTO {

        // TODO: fields, once gruppen1 adds them

    }
}
