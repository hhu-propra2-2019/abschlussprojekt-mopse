package mops.businesslogic.gruppenbildung;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mops.businesslogic.exception.GruppenbildungsException;
import mops.exception.MopsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Service to call REST-API from Gruppenbildung.
 */
@Slf4j
@Service
@Profile("prod")
@RequiredArgsConstructor
public class GruppenbildungsService {

    /**
     * REST-API-URL of gruppen1.
     */
    @Value("${material1.mops.gruppenbildung.url}")
    @SuppressWarnings({ "PMD.ImmutableField", "PMD.BeanMembersShouldSerialize" })
    private String gruppenbildungsUrl = "https://mops.hhu.de/gruppen1";

    /**
     * Allows to send REST API calls.
     */
    private final RestTemplate restTemplate;

    /**
     * Does a group exist.
     *
     * @param groupId group id to test
     * @return true if it exists
     */
    @SuppressWarnings({ "PMD.AvoidCatchingGenericException", "PMD.LawOfDemeter" })
    @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE", justification = "NPE is intended and caught")
    public boolean doesGroupExist(UUID groupId) throws MopsException {
        try {
            Map<String, Boolean> result = restTemplate.exchange(
                    gruppenbildungsUrl + "/isUserAdminInGroup?groupId={groupId}",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Boolean>>() {
                    },
                    String.valueOf(groupId)
            ).getBody();
            return Objects.requireNonNull(result, "got null response from GET")
                    .get("doesGroupExist");
        } catch (Exception e) {
            log.error("Error while doing API call 'doesGroupExist' to Gruppenbildung:", e);
            throw new GruppenbildungsException("Fehler beim Aufruf von 'doesGroupExist'.", e);
        }
    }

    /**
     * Is a user member in a group.
     *
     * @param userName user name
     * @param groupId  group id
     * @return true if the user is a member in the given group
     */
    @SuppressWarnings({ "PMD.AvoidCatchingGenericException", "PMD.LawOfDemeter" })
    @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE", justification = "NPE is intended and caught")
    public boolean isUserInGroup(String userName, UUID groupId) throws MopsException {
        try {
            Map<String, Boolean> result = restTemplate.exchange(
                    gruppenbildungsUrl + "/isUserInGroup?userName={userName}&groupId={groupId}",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Boolean>>() {
                    },
                    userName,
                    String.valueOf(groupId)
            ).getBody();
            return Objects.requireNonNull(result, "got null response from GET")
                    .get("isUserInGroup");
        } catch (Exception e) {
            log.error("Error while doing API call 'isUserInGroup' to Gruppenbildung:", e);
            throw new GruppenbildungsException("Fehler beim Aufruf von 'isUserInGroup'.", e);
        }
    }

    /**
     * Is a user admin in a group.
     *
     * @param userName user name
     * @param groupId  group id
     * @return true if the user is an admin in the given group
     */
    @SuppressWarnings({ "PMD.AvoidCatchingGenericException", "PMD.LawOfDemeter" })
    @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE", justification = "NPE is intended and caught")
    public boolean isUserAdminInGroup(String userName, UUID groupId) throws MopsException {
        try {
            Map<String, Boolean> result = restTemplate.exchange(
                    gruppenbildungsUrl + "/isUserAdminInGroup?userName={userName}&groupId={groupId}",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Boolean>>() {
                    },
                    userName,
                    String.valueOf(groupId)
            ).getBody();
            return Objects.requireNonNull(result, "got null response from GET")
                    .get("isUserAdminInGroup");
        } catch (Exception e) {
            log.error("Error while doing API call 'isUserAdminInGroup' to Gruppenbildung:", e);
            throw new GruppenbildungsException("Fehler beim Aufruf von 'isUserAdminInGroup'.", e);
        }
    }

    /**
     * Gets all updated groups since the last event timestamp.
     *
     * @param lastEventId last event timestamp
     * @return object that contains all updated groups
     */
    @SuppressWarnings({ "PMD.AvoidCatchingGenericException", "PMD.LawOfDemeter" })
    public UpdatedGroupsDTO getUpdatedGroups(long lastEventId) throws MopsException {
        try {
            return restTemplate.exchange(
                    gruppenbildungsUrl + "/returnAllGroups?lastEventId={lastEventid}",
                    HttpMethod.GET,
                    null,
                    UpdatedGroupsDTO.class,
                    String.valueOf(lastEventId)
            ).getBody();
        } catch (Exception e) {
            log.error("Error while doing API call 'returnAllGroups' to Gruppenbildung:", e);
            throw new GruppenbildungsException("Fehler beim Aufruf von 'returnAllGroups'.", e);
        }
    }

    /**
     * Gets all members of a given group.
     *
     * @param groupId group id
     * @return list of members
     */
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public List<UserDTO> getMembers(UUID groupId) throws MopsException {
        try {
            return restTemplate.exchange(
                    gruppenbildungsUrl + "/returnUsersOfGroup?groupId={groupId}",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<UserDTO>>() {
                    },
                    String.valueOf(groupId)
            ).getBody();
        } catch (Exception e) {
            log.error("Error while doing API call 'returnUsersOfGroup' to Gruppenbildung:", e);
            throw new GruppenbildungsException("Fehler beim Aufruf von 'returnUsersOfGroup'.", e);
        }
    }

    /**
     * Gets all groups of a given user.
     *
     * @param userName user name
     * @return list of groups
     */
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public List<GroupDTO> getUserGroups(String userName) throws MopsException {
        try {
            return restTemplate.exchange(
                    gruppenbildungsUrl + "/returnGroupsOfUsers?userName={userName}",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<GroupDTO>>() {
                    },
                    userName
            ).getBody();
        } catch (Exception e) {
            log.error("Error while doing API call 'returnGroupsOfUsers' to Gruppenbildung:", e);
            throw new GruppenbildungsException("Fehler beim Aufruf von 'returnGroupsOfUsers'.", e);
        }
    }
}
