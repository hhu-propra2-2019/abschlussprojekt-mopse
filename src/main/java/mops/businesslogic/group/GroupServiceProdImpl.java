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
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
     * URL to GruppenFindung.
     */
    @Value("${material1.mops.gruppenfindung.url}")
    private String gruppenFindungUrl = "https://mops.hhu.de/gruppe1";

    /**
     * Allows to send REST API calls.
     */
    private final RestTemplate restTemplate;

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.LawOfDemeter")
    public String fetchRoleForUserInGroup(Account account, long groupId) throws MopsException {
        log.info("Request role for user {} in group {}", account.getName(), groupId);
        Permission permission = restTemplate.getForObject(gruppenFindungUrl + "/get-permission", Permission.class);
        if (permission == null) {
            log.error("The request for user roles for user '{}' in group {} failed.", account.getName(), groupId);
            throw new GruppenFindungException(String.format(
                    "Es konnte keine Rolle für Sie in Gruppe %d gefunden werden.",
                    groupId));
        }
        return permission.getRoleInGroup(groupId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.LawOfDemeter") // stream
    public Set<String> fetchRolesInGroup(long groupId) throws MopsException {
        log.info("Request roles for group {}", groupId);
        GroupPermission[] groupPermissions = restTemplate.getForObject(gruppenFindungUrl + "/get-roles",
                GroupPermission[].class);
        if (groupPermissions == null) {
            log.error("The request for roles in group {} failed.", groupId);
            throw new GruppenFindungException(String.format(
                    "Es konnten keinen Rollen für diese Gruppe %d gefunden werden.",
                    groupId));
        }
        return Arrays.stream(groupPermissions)
                .map(GroupPermission::getPermission)
                .collect(Collectors.toSet());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Group> getAllGroups() throws MopsException {
        // TODO: change to real route once known
        Group[] groups = restTemplate.getForObject(gruppenFindungUrl + "/get-all-groups", Group[].class);
        if (groups == null) {
            log.error("The request for all groups failed.");
            throw new GruppenFindungException("Es konnten keinen Gruppen gefunden werden.");
        }
        return List.of(groups);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Group> getAllGroupsOfUser(Account account) throws MopsException {
        // TODO: change to real route once known
        Group[] groups = restTemplate.getForObject(gruppenFindungUrl + "/get-all-groups-from-user", Group[].class);
        if (groups == null) {
            log.error("The request for groups of user {} failed.", account.getName());
            throw new GruppenFindungException(String.format(
                    "Es konnten keinen Gruppen für die Nutzerin '%s' gefunden werden.",
                    account.getName()));
        }
        return List.of(groups);
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
                    .filter(group -> group.getGroup() == groupId)
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
