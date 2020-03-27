package mops.businesslogic.directory;

import mops.businesslogic.group.GroupService;
import mops.businesslogic.security.Account;
import mops.exception.MopsException;
import mops.persistence.DirectoryPermissionsRepository;
import mops.persistence.FileInfoRepository;
import mops.persistence.FileRepository;
import mops.persistence.directory.Directory;
import mops.persistence.group.Group;
import mops.persistence.permission.DirectoryPermissions;
import mops.util.DbContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@DbContext
@SpringBootTest
class DeleteServiceTest {

    static final String STUDENTIN = "studentin";
    static final String ADMIN = "admin";
    static final String EDITOR = "editor";
    static final String VIEWER = "viewer";
    static final String INTRUDER = "intruder";
    static final long GROUP_ID = 1L;
    static final UUID GROUP_UUID = new UUID(0, 1L);

    @MockBean
    GroupService groupService;
    @MockBean
    FileRepository fileRepository;

    @Autowired
    DeleteService deleteService;
    @Autowired
    DirectoryService directoryService;
    @Autowired
    DirectoryPermissionsRepository directoryPermissionsRepository;
    @Autowired
    FileInfoRepository fileInfoRepository;

    Directory root;
    Account admin;
    Account editor;
    Account user;

    /**
     * Prepares user accounts.
     */
    @BeforeEach
    void setup() throws MopsException {
        user = Account.of(VIEWER, "user@hhu.de", STUDENTIN);
        editor = Account.of(EDITOR, "editor@hhu.de", STUDENTIN);
        admin = Account.of(ADMIN, "admin@hhu.de", STUDENTIN);

        given(groupService.getRoles(GROUP_ID)).willReturn(Set.of(ADMIN, EDITOR, VIEWER));

        Group group = Group.builder()
                .id(GROUP_ID)
                .groupId(GROUP_UUID)
                .name("Test Group")
                .member(admin.getName(), ADMIN)
                .member(editor.getName(), EDITOR)
                .member(user.getName(), VIEWER)
                .build();

        given(groupService.getGroup(GROUP_ID)).willReturn(group);
        given(groupService.getDefaultPermissions(GROUP_ID)).willReturn(
                DirectoryPermissions.builder()
                        .entry(ADMIN, true, true, true)
                        .entry(EDITOR, true, true, false)
                        .entry(VIEWER, true, false, false)
                        .build()
        );

        root = directoryService.getOrCreateRootFolder(GROUP_ID).getRootDir();
    }

    /**
     * Tests if a admin can delete subfolder.
     */
    @Test
    void deleteFirstFolderTest() throws MopsException {
        Directory subFolder = directoryService.createFolder(admin, root.getId(), "a");
        long permissionsId = subFolder.getPermissionsId();

        Directory parent = deleteService.deleteFolder(admin, subFolder.getId());

        List<Directory> subFolders = directoryService.getSubFolders(admin, root.getId());
        Optional<DirectoryPermissions> byId = directoryPermissionsRepository.findById(permissionsId);

        assertThat(parent).isEqualTo(root);
        assertThat(subFolders).isEmpty();
        assertThat(byId).isEmpty();
    }

    @Test
    void deleteRootFolderTest() throws MopsException {
        long id = 2L;
        UUID groupId = new UUID(0, 2);

        Group group = Group.builder()
                .id(id)
                .groupId(groupId)
                .name("Another Test Group")
                .member(admin.getName(), ADMIN)
                .member(editor.getName(), EDITOR)
                .member(user.getName(), VIEWER)
                .build();

        given(groupService.getRoles(id)).willReturn(Set.of(ADMIN, EDITOR, VIEWER));
        given(groupService.getGroup(id)).willReturn(group);
        given(groupService.getDefaultPermissions(id)).willReturn(
                DirectoryPermissions.builder()
                        .entry(ADMIN, true, true, true)
                        .entry(EDITOR, true, true, false)
                        .entry(VIEWER, true, false, false)
                        .build()
        );

        Directory rootFolder = directoryService.getOrCreateRootFolder(id).getRootDir();
        long permissionsId = rootFolder.getPermissionsId();
        Directory directory = deleteService.deleteFolder(admin, rootFolder.getId());
        Optional<DirectoryPermissions> byId = directoryPermissionsRepository.findById(permissionsId);

        assertThat(directory).isNull();
        assertThat(byId).isEmpty();
    }

    @Test
    void deleteSecondLevelFolderTest() throws MopsException {
        Directory subFolder = directoryService.createFolder(admin, root.getId(), "a");
        Directory secondLevel = directoryService.createFolder(admin, subFolder.getId(), "b");
        long permissionsId = secondLevel.getPermissionsId();

        Directory parent = deleteService.deleteFolder(admin, secondLevel.getId());
        List<Directory> subFolders = directoryService.getSubFolders(admin, subFolder.getId());

        assertThat(parent).isEqualTo(subFolder);
        assertThat(subFolders).isEmpty();
        Optional<DirectoryPermissions> byId = directoryPermissionsRepository.findById(permissionsId);
        assertThat(byId).isNotEmpty();
    }
}
