package mops.businesslogic.group;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mops.businesslogic.exception.GruppenFindungException;
import mops.businesslogic.security.Account;
import mops.exception.MopsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
     * Represents the role of an admin.
     */
    @Value("${material1.mops.configuration.role.viewer}")
    private String viewerRole = "viewer";

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
        try {
            Boolean result = restTemplate.getForObject(gruppenfindungsUrl + "/isUserAdminInGroup?groupId={groupId}",
                    Boolean.class,
                    String.valueOf(groupId)
            );
            return Objects.requireNonNull(result, "got null response from GET");
        } catch (Exception e) {
            throw new MopsException("...", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.LawOfDemeter")
    public String getUserRole(Account account, long groupId) throws MopsException {
        log.debug("Request role for user '{}' in group '{}'.", account.getName(), groupId);
        throw new UnsupportedOperationException("nyi");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.LawOfDemeter") // stream
    public Set<String> getRoles(long groupId) throws MopsException {
        log.debug("Request roles in group '{}'", groupId);
        throw new UnsupportedOperationException("nyi");
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

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    private boolean isUserInGroup(Account account, long groupId) throws MopsException {
        try {
            Boolean result = restTemplate.getForObject(
                    gruppenfindungsUrl + "/isUserInGroup?userName={userName}&groupId={groupId}",
                    Boolean.class,
                    account.getName(),
                    String.valueOf(groupId)
            );
            return Objects.requireNonNull(result, "got null response from GET");
        } catch (Exception e) {
            throw new MopsException("...", e);
        }
    }

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    private boolean isUserAdminInGroup(Account account, long groupId) throws MopsException {
        try {
            Boolean result = restTemplate.getForObject(
                    gruppenfindungsUrl + "/isUserAdminInGroup?userName={userName}&groupId={groupId}",
                    Boolean.class,
                    account.getName(),
                    String.valueOf(groupId)
            );
            return Objects.requireNonNull(result, "got null response from GET");
        } catch (Exception e) {
            throw new MopsException("...", e);
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
            throw new MopsException("...", e);
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
            throw new MopsException("...", e);
        }
    }

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    private List<GroupDTO> returnGroupsOfUsers(Account account) throws MopsException {
        try {
            return restTemplate.exchange(
                    gruppenfindungsUrl + "/returnGroupsOfUsers?userName={userName}",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<GroupDTO>>() {
                    },
                    account.getName()
            ).getBody();
        } catch (Exception e) {
            throw new MopsException("...", e);
        }
    }

    static class GroupDTO {
        // TODO: fields, once gruppen1 adds them
    }

    static class UserDTO {
        // TODO: fields, once gruppen1 adds them
    }

    /**
     * Private class that represents all roles of a user.
     */
    @AllArgsConstructor
    @Slf4j
    @SuppressWarnings("PMD.LawOfDemeter")
    static class Permission {

        /**
         * User name.
         */
        private String user;
        /**
         * Groups the is in.
         */
        private Set<GroupPermission> groups;

        /**
         * Gets roles for a group.
         *
         * @param groupId id of the group
         * @return the role of the user in the group
         * @throws GruppenFindungException something went wrong during api request
         */
        @SuppressWarnings({ "PMD.UnnecessaryLocalBeforeReturn", "PMD.DataflowAnomalyAnalysis" })
        public String getRoleInGroup(long groupId) throws GruppenFindungException {
            Optional<GroupPermission> first = groups.stream()
                    .filter(groupPermission -> groupPermission.getGroup() == groupId)
                    .findFirst();
            GroupPermission groupPermission = first.orElseThrow(() -> {
                log.error("Unable to find group with the {} at GruppenFindung1.", groupId);
                return new GruppenFindungException(String.format(
                        "Gruppe mit der id %d konnte nicht gefunden werden.", groupId));
            });
            return groupPermission.getPermission();
        }
    }

    /**
     * Private class that represents the role a user has in a group.
     */
    @Getter
    @AllArgsConstructor
    static class GroupPermission {

        /**
         * Id of the group.
         */
        private long group;
        /**
         * Permission/Role in that group.
         */
        private String permission;

    }
}
