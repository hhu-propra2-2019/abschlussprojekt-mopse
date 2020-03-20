package mops.businesslogic;

import lombok.AllArgsConstructor;
import mops.exception.MopsException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class GroupServiceDevImpl implements GroupService {

    /**
     * Directory Service.
     */
    private DirectoryService directoryService;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Group> getAllGroups() throws MopsException {
        long groupId = 100L;
        Group einzigen = new Group(groupId, "Einzigen");
        return List.of(einzigen);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Group> getAllGroups(Account account) throws MopsException {
        long groupId = 100L;
        Group einzigen = new Group(groupId, "Einzigen");
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
