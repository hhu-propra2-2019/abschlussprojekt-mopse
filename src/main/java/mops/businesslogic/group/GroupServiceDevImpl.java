package mops.businesslogic.group;

import lombok.AllArgsConstructor;
import mops.businesslogic.directory.DirectoryService;
import mops.businesslogic.security.Account;
import mops.exception.MopsException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@inheritDoc}
 * This is used during development and will return dummies.
 */
@Service
@AllArgsConstructor
@Profile({ "dev", "test" })
public class GroupServiceDevImpl implements GroupService {

    /**
     * Group id.
     */
    private static final Set<Long> VALID_GROUP_IDS = Set.of(100L);

    /**
     * Directory Service.
     */
    private DirectoryService directoryService;

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.LawOfDemeter") // stream
    public List<Group> getAllGroups() throws MopsException {
        return VALID_GROUP_IDS.stream()
                .map(id -> new Group(id, "Einzigen #" + id))
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Group> getAllGroupsOfUser(Account account) throws MopsException {
        return getAllGroups(); // every user is in every group
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
