package mops.businesslogic.group;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mops.businesslogic.exception.DatabaseException;
import mops.businesslogic.security.Account;
import mops.exception.MopsException;
import mops.persistence.GroupRepository;
import mops.persistence.group.Group;
import mops.persistence.permission.DirectoryPermissions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * {@inheritDoc}
 * This is used during production.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Profile("prod")
@SuppressWarnings("PMD.UnusedPrivateMethod")
public class GroupServiceProdImpl implements GroupService {

    /**
     * Represents the role of an admin.
     */
    @Value("${material1.mops.configuration.role.admin}")
    @SuppressWarnings({ "PMD.ImmutableField", "PMD.BeanMembersShouldSerialize" })
    private String adminRole;
    /**
     * Represents the role of a viewer.
     */
    @Value("${material1.mops.configuration.role.viewer}")
    @SuppressWarnings({ "PMD.ImmutableField", "PMD.BeanMembersShouldSerialize" })
    private String viewerRole;

    /**
     * Access to our Group Database.
     */
    private final GroupRepository groupRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public boolean doesGroupExist(long groupId) throws MopsException {
        log.debug("Request existence of group '{}'.", groupId);
        try {
            return groupRepository.findById(groupId).isPresent();
        } catch (Exception e) {
            log.error("Error while checking group existence for group {}:", groupId, e);
            throw new DatabaseException("Konnte nicht auf die Existenz der Gruppe testen.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.LawOfDemeter") // stream
    public Set<String> getRoles(long groupId) throws MopsException {
        log.debug("Request roles in group '{}'", groupId);
        return Set.of(adminRole, viewerRole);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Group> getAllGroups() throws MopsException {
        log.debug("Request all groups.");
        List<Group> groups = new ArrayList<>();
        try {
            groupRepository.findAll().forEach(groups::add);
        } catch (Exception e) {
            log.error("Error while getting all groups:", e);
            throw new DatabaseException("Konnte nicht alle Gruppen laden.", e);
        }
        return groups;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Group> getUserGroups(Account account) throws MopsException {
        log.debug("Request all groups of user '{}'.", account.getName());
        try {
            return groupRepository.findByUser(account.getName());
        } catch (Exception e) {
            log.error("Error while getting all groups for user {}:", account.getName(), e);
            throw new DatabaseException("Konnte nicht alle Gruppen eines Benutzers laden.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "PMD.LawOfDemeter" }) //builder
    public DirectoryPermissions getDefaultPermissions(long groupId) {
        return DirectoryPermissions.builder()
                .entry(adminRole, true, true, true)
                .entry(viewerRole, true, false, false)
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "PMD.LawOfDemeter", "PMD.AvoidCatchingGenericException" })
    public Group getGroup(long groupId) throws MopsException {
        try {
            return groupRepository.findById(groupId).orElseThrow();
        } catch (Exception e) {
            log.error("Failed to retrieve group with id '{}':", groupId, e);
            throw new DatabaseException(
                    "Die Gruppe konnte nicht gefunden werden, bitte versuchen sie es später nochmal!", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "PMD.LawOfDemeter", "PMD.AvoidCatchingGenericException" })
    public List<Group> saveAllGroups(Collection<Group> groups) throws MopsException {
        List<Group> saved = new ArrayList<>();
        try {
            groupRepository.saveAll(groups).forEach(saved::add);
        } catch (Exception e) {
            log.error("Failed to save {} groups:", groups.size(), e);
            throw new DatabaseException("Die Gruppen konnte nicht gespeichert werden!", e);
        }
        return saved;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "PMD.LawOfDemeter", "PMD.AvoidCatchingGenericException" })
    public void deleteAllGroups(Collection<Long> groupIds) throws MopsException {
        try {
            groupIds.forEach(groupRepository::deleteById);
        } catch (Exception e) {
            log.error("Failed to delete {} groups:", groupIds.size(), e);
            throw new DatabaseException("Die Gruppen konnte nicht gelöscht werden!", e);
        }
    }
}
