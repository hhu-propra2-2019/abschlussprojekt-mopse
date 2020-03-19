package mops.businesslogic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mops.exception.MopsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

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
     * Directory Service.
     */
    private final DirectoryService directoryService;

    /**
     * Allows to send REST API calls.
     */
    private final RestTemplate restTemplate;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Group> getAllGroups(Account account) throws MopsException {
        // TODO: change to real route once known
        Group[] groups = restTemplate.getForObject(gruppenFindungUrl + "/get-all", Group[].class);
        if (groups == null) {
            log.error("The request for groups of user {} failed.", account.getName());
            throw new GruppenFindungException(String.format(
                    "Es konnten keinen Gruppen für die Nutzerin '%s' gefunden werden.",
                    account.getName()));
        }
        return List.of(groups);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.LawOfDemeter")
    public GroupRootDirWrapper getGroupUrl(long groupId) throws MopsException {
        return new GroupRootDirWrapper(groupId, directoryService.getOrCreateRootFolder(groupId).getId());
    }
}
