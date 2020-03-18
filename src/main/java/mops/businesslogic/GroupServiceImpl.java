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
     * API to GruppenFindung.
     */
    private PermissionService permissionService;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Group> getAllGroups(Account account) throws MopsException {
        return permissionService.fetchGroupsForUser(account);
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
