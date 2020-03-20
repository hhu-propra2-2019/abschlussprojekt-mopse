package mops.businesslogic;

import lombok.AllArgsConstructor;
import mops.exception.MopsException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * {@inheritDoc}
 * This is used during development and will return dummies.
 */
@Service
@AllArgsConstructor
public class GroupServiceDevImpl implements GroupService {
    /**
     * constant for the long value 100.
     */
    private static final long GROUPID = 100L;

    /**
     * Directory Service.
     */
    private DirectoryService directoryService;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Group> getAllGroups() throws MopsException {
        Group einzigen = new Group(GROUPID, "Einzigen");
        return List.of(einzigen);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Group> getAllGroupsOfUser(Account account) throws MopsException {
        Group einzigen = new Group(GROUPID, "Einzigen");
        return List.of(einzigen);
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
