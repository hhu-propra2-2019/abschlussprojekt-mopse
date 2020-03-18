package mops.businesslogic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import mops.exception.MopsException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@SuppressWarnings("PMD.LawOfDemeter")
@Service
@Profile("prod")
public class PermissionServiceProdImpl implements PermissionService {

    //TODO: this needs to be changed to the actual address and we need a provisional during development.
    /**
     * URL to GruppenFindung.
     */
    public static final String URL = "https://mops.hhu.de/gruppe1/";

    /**
     * A rest template to send request to external api.
     */
    private final RestTemplate restTemplate;

    /**
     * {@inheritDoc}
     */
    @Override
    public String fetchRoleForUserInGroup(Account account, long groupId) throws MopsException {
        log.info("Request role for user {} in group {}", account.getName(), groupId);
        Permission permission = restTemplate.getForObject(URL, Permission.class);
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
    public Set<String> fetchRolesInGroup(long groupId) throws MopsException {
        log.info("Request roles for group {}", groupId);
        GroupPermission[] groupPermissions = restTemplate.getForObject(URL, GroupPermission[].class);
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
