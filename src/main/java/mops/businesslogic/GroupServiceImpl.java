package mops.businesslogic;

import lombok.AllArgsConstructor;
import mops.exception.MopsException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class GroupServiceImpl implements GroupService {

    /**
     * Directory Service.
     */
    private DirectoryService directoryService;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Group> getAllGroups(Account account) throws MopsException {
        // TODO: get groups from Gruppenfindung
        return List.of();
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
