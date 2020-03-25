package mops.businesslogic.group;

import lombok.Data;
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

import java.util.*;

/**
 * {@inheritDoc}
 * This is used during production.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Profile("prod")
@SuppressWarnings("PMD.UnusedPrivateMethod")
public class GroupServiceProdImpl implements GroupService {

    /**
     * Update rate of database. Currently one minute.
     */
    private static final long UPDATE_RATE = 60L * 1000L;

    /**
     * REST-API-URL of gruppen1.
     */
    @Value("${material1.mops.gruppenfindung.url}")
    @SuppressWarnings({ "PMD.ImmutableField", "PMD.BeanMembersShouldSerialize" })
    private String gruppenfindungsUrl = "https://mops.hhu.de/gruppen1";
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
    public boolean doesGroupExist(UUID groupId) throws MopsException {
        log.debug("Request existence of group '{}'.", groupId);
        throw new MopsException("noch nicht implementiert", new UnsupportedOperationException("nyi"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.LawOfDemeter") // stream
    public Set<String> getRoles(UUID groupId) throws MopsException {
        log.debug("Request roles in group '{}'", groupId);
        throw new MopsException("noch nicht implementiert", new UnsupportedOperationException("nyi"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Group> getAllGroups() throws MopsException {
        log.debug("Request all groups.");
        throw new MopsException("noch nicht implementiert", new UnsupportedOperationException("nyi"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Group> getUserGroups(Account account) throws MopsException {
        log.debug("Request all groups of user '{}'.", account.getName());
        throw new MopsException("noch nicht implementiert", new UnsupportedOperationException("nyi"));
    }

    /**
     * Scheduled function to update our database the external group event stream.
     */
    @Transactional
    @Scheduled(fixedRate = UPDATE_RATE)
    @SuppressWarnings("checkstyle:DesignForExtension")
    public void updateDatabase() throws MopsException {
        // TODO: implement with timestamp and deltas once the gruppen1 API is implemented
        /*try {

        } catch (MopsException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error("Error while updating group database:", e);
        }*/
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "PMD.LawOfDemeter", "PMD.AvoidCatchingGenericException", "PMD.DefaultPackage" })
    public Group getGroup(UUID groupId) throws MopsException {
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
    private boolean doesGroupExistExternal(UUID groupId) throws MopsException {
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
    private boolean isUserInGroup(String userName, UUID groupId) throws MopsException {
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
    private boolean isUserAdminInGroup(String userName, UUID groupId) throws MopsException {
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

    @SuppressWarnings({ "PMD.AvoidCatchingGenericException", "PMD.LawOfDemeter" })
    private UpdatedGroupsDTO returnAllGroups(long lastEventId) throws MopsException {
        try {
            return restTemplate.exchange(
                    gruppenfindungsUrl + "/returnAllGroups?lastEventId={lastEventid}",
                    HttpMethod.GET,
                    null,
                    UpdatedGroupsDTO.class,
                    String.valueOf(lastEventId)
            ).getBody();
        } catch (Exception e) {
            throw new GruppenfindungsException("...", e);
        }
    }

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    private List<UserDTO> returnUsersOfGroup(UUID groupId) throws MopsException {
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

    /**
     * Updated groups since the last time stamp.
     */
    @Data
    @SuppressWarnings("PMD.DefaultPackage")
    static class UpdatedGroupsDTO {

        /**
         * Current event timestamp id.
         */
        private long eventId;
        /**
         * List of groups.
         */
        private List<GroupDTO> groupDAOs;

    }

    /**
     * Group.
     */
    @Data
    @SuppressWarnings("PMD.DefaultPackage")
    static class GroupDTO {

        /**
         * Group id.
         */
        private UUID groupId;
        /**
         * Course.
         */
        private String course;
        /**
         * Group description.
         */
        private String groupDescription;
        /**
         * Group name.
         */
        private String groupName;
        /**
         * Group status.
         */
        private StatusDTO status;

    }

    /**
     * Group status enum.
     */
    @SuppressWarnings("PMD.DefaultPackage")
    enum StatusDTO {

        /**
         * Represents an active group.
         */
        ACTIVE,
        /**
         * Represents a deleted group.
         */
        DEACTIVATED

    }

    /**
     * User.
     */
    @Data
    @SuppressWarnings("PMD.DefaultPackage")
    static class UserDTO {

        /**
         * User name.
         */
        private String username;

    }
}
