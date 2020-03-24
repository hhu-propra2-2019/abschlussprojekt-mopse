package mops.businesslogic.directory;

import mops.businesslogic.exception.ReadAccessPermissionException;
import mops.businesslogic.file.query.FileQuery;
import mops.businesslogic.group.GroupService;
import mops.businesslogic.security.Account;
import mops.exception.MopsException;
import mops.persistence.DirectoryPermissionsRepository;
import mops.persistence.FileInfoRepository;
import mops.persistence.FileRepository;
import mops.persistence.directory.Directory;
import mops.persistence.file.FileInfo;
import mops.persistence.permission.DirectoryPermissions;
import mops.util.DbContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;

@DbContext
@SpringBootTest
class DirectoryServiceTest {

    static final String STUDENTIN = "studentin";
    static final String ADMIN = "admin";
    static final String EDITOR = "editor";
    static final String USER = "user";
    static final String INTRUDER = "intruder";
    static final long GROUP_ID = 0L;

    @MockBean
    GroupService groupService;
    @MockBean
    FileRepository fileRepository;

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
    Account intruder;

    /**
     * Prepares user accounts.
     */
    @BeforeEach
    void setup() throws MopsException {
        intruder = Account.of(INTRUDER, "intruder@uni-koeln.de", STUDENTIN);
        user = Account.of(USER, "user@hhu.de", STUDENTIN);
        editor = Account.of(EDITOR, "editor@hhu.de", STUDENTIN);
        admin = Account.of(ADMIN, "admin@hhu.de", STUDENTIN);

        given(groupService.fetchRolesInGroup(GROUP_ID)).willReturn(Set.of(ADMIN, EDITOR, USER));
        given(groupService.fetchRoleForUserInGroup(admin, GROUP_ID)).willReturn(ADMIN);
        given(groupService.fetchRoleForUserInGroup(editor, GROUP_ID)).willReturn(EDITOR);
        given(groupService.fetchRoleForUserInGroup(user, GROUP_ID)).willReturn(USER);
        given(groupService.fetchRoleForUserInGroup(intruder, GROUP_ID)).willReturn(INTRUDER);

        root = directoryService.getOrCreateRootFolder(GROUP_ID).getRootDir();
    }

    /**
     * Test if a group folder is correctly created.
     */
    @Test
    void createGroupRootFolder() {
        Directory expected = Directory.builder()
                .name("")
                .groupOwner(GROUP_ID)
                .permissions(root.getPermissionsId())
                .build();

        assertThat(root).isEqualToIgnoringGivenFields(expected, "id", "creationTime", "lastModifiedTime");
    }

    /**
     * Test if folder is created in a given root folder.
     */
    @Test
    void createFolderTest() throws MopsException {
        String subDirName = "a";

        Directory expected = Directory.builder()
                .fromParent(root)
                .name(subDirName)
                .build();

        Directory subDir = directoryService.createFolder(admin, root.getId(), subDirName);

        assertThat(subDir).isEqualToIgnoringGivenFields(expected,
                "id",
                "permissionsId",
                "creationTime",
                "lastModifiedTime");
        assertThat(subDir.getPermissionsId()).isNotEqualTo(root.getPermissionsId());
    }

    /**
     * Test if admin can update the permissions of a directory.
     */
    @Test
    void updatePermissionTest() throws MopsException {
        DirectoryPermissions permissions = DirectoryPermissions.builder()
                .entry(USER, true, false, false)
                .entry(EDITOR, true, false, false)
                .entry(ADMIN, true, true, true)
                .build();

        DirectoryPermissions newPermissions = directoryService.updatePermission(admin, root.getId(), permissions);

        assertThat(newPermissions).isEqualToIgnoringGivenFields(permissions, "id", "creationTime", "lastModifiedTime");
    }

    /**
     * Test if sub folders are correctly returned.
     */
    @Test
    void getSubFoldersTest() throws MopsException {
        String subDirName1 = "a";
        String subDirName2 = "b";

        Directory createdFirstDir = directoryService.createFolder(admin, root.getId(), subDirName1);
        Directory createdSecondDir = directoryService.createFolder(admin, root.getId(), subDirName2);

        List<Directory> subFolders = directoryService.getSubFolders(admin, root.getId());

        assertThat(subFolders).containsExactlyInAnyOrder(createdFirstDir, createdSecondDir);
    }

    /**
     * Checks if exception is thrown if the user does not have reading permission.
     */
    @Test
    void getSubFoldersWithoutPermissionTest() {
        assertThatExceptionOfType(ReadAccessPermissionException.class)
                .isThrownBy(() -> directoryService.getSubFolders(intruder, root.getId()));
    }

    /**
     * Test if a user with read only permission can't create a sub folder.
     */
    @Test
    void createSubFolderWithReadsOnlyPermissionTest() throws MopsException {
        DirectoryPermissions permissions = DirectoryPermissions.builder()
                .entry(USER, false, false, false)
                .entry(EDITOR, true, false, false)
                .entry(ADMIN, true, true, true)
                .build();

        directoryService.updatePermission(admin, root.getId(), permissions);

        assertThatExceptionOfType(ReadAccessPermissionException.class)
                .isThrownBy(() -> directoryService.getSubFolders(user, root.getId()));
    }

    /**
     * Tests if a admin can delete subfolder.
     */
    @Test
    void deleteSubFolderTest() throws MopsException {
        Directory subFolder = directoryService.createFolder(admin, root.getId(), "a");

        Directory parent = directoryService.deleteFolder(admin, subFolder.getId());

        List<Directory> subFolders = directoryService.getSubFolders(admin, root.getId());

        assertThat(parent).isEqualTo(root);
        assertThat(subFolders).isEmpty();
    }

    @Test
    void searchFolderTest() throws MopsException {
        FileQuery query = FileQuery.builder()
                .name("a")
                .build();

        FileInfo matchingFile = fileInfoRepository.save(
                FileInfo.builder()
                        .name("a")
                        .directory(root)
                        .type("txt")
                        .size(0L)
                        .owner(USER)
                        .build()
        );
        fileInfoRepository.save(
                FileInfo.builder()
                        .name("b")
                        .directory(root)
                        .type("txt")
                        .size(0L)
                        .owner(USER)
                        .build()
        );

        List<FileInfo> fileInfos = directoryService.searchFolder(user, root.getId(), query);

        assertThat(fileInfos).containsExactlyInAnyOrder(matchingFile);
    }

    @Test
    void searchFolderWithoutPermissionTest() throws MopsException {
        FileQuery fileQuery = FileQuery.builder()
                .build();

        assertThatExceptionOfType(ReadAccessPermissionException.class)
                .isThrownBy(() -> directoryService.searchFolder(intruder, root.getId(), fileQuery));
    }
}
