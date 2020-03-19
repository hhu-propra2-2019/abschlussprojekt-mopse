package mops.businesslogic;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mops.exception.MopsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
@Profile("prod")
public class GroupServiceProdImpl implements GroupService {
    /**
     * URL to GruppenFindung.
     */
    @Value("${material1.mops.gruppenfindung.url}")
    public static String gruppenFindungUrl;

    /**
     * Directory Service.
     */
    private DirectoryService directoryService;

    /**
     * Allows to send REST API calls.
     */
    private RestTemplate restTemplate;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Group> getAllGroups(Account account) throws MopsException {
        Group[] groups = restTemplate.getForObject(gruppenFindungUrl, Group[].class);
        if (groups == null) {
            log.error("The request for groups of user {} failed.", account.getName());
            throw new GruppenFindungException(String.format(
                    "Es konnten keinen Gruppen für diese Nutzerin %s gefunden werden.",
                    account.getName()));
        }
        return Arrays.asList(groups);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.LawOfDemeter")
    public GroupRootDirWrapper getGroupUrl(Account account, long groupId) throws MopsException {
        return new GroupRootDirWrapper(groupId, directoryService.getOrCreateRootFolder(account, groupId).getId());
    }
}
