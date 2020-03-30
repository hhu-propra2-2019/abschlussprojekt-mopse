package mops.businesslogic.directory;

import mops.businesslogic.exception.EmptyNameException;
import mops.businesslogic.exception.ReadAccessPermissionException;
import mops.businesslogic.exception.WriteAccessPermissionException;
import mops.businesslogic.file.query.FileQuery;
import mops.businesslogic.group.GroupService;
import mops.businesslogic.security.Account;
import mops.exception.MopsException;
import mops.persistence.DirectoryPermissionsRepository;
import mops.persistence.FileInfoRepository;
import mops.persistence.FileRepository;
import mops.persistence.directory.Directory;
import mops.persistence.file.FileInfo;
import mops.persistence.group.Group;
import mops.persistence.permission.DirectoryPermissions;
import mops.util.DbContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@DbContext
@SpringBootTest
class DirectoryServiceTest {

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
     * Test if a group folder is correctly created.
     */
    @Test
    void createGroupRootFolder() {
        Directory expected = Directory.builder()
                .name("Test Group")
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

    @Test
    void createSecondLevelFolderTest() throws MopsException {
        String subDirName = "a";
        String secondLevelName = "b";

        Directory subDir = directoryService.createFolder(admin, root.getId(), subDirName);

        Directory expected = Directory.builder()
                .fromParent(subDir)
                .name(subDirName)
                .build();

        Directory secondLevelFolder = directoryService.createFolder(admin, subDir.getId(), secondLevelName);
        assertThat(secondLevelFolder).isEqualToIgnoringGivenFields(expected,
                "id",
                "name",
                "creationTime",
                "lastModifiedTime");
        assertThat(secondLevelFolder.getPermissionsId()).isNotEqualTo(root.getPermissionsId());
    }

    /**
     * Test if admin can update the permissions of a directory.
     */
    @Test
    void updatePermissionTest() throws MopsException {
        DirectoryPermissions permissions = DirectoryPermissions.builder()
                .entry(VIEWER, true, false, false)
                .entry(EDITOR, true, false, false)
                .entry(ADMIN, true, true, true)
                .build();

        DirectoryPermissions newPermissions = directoryService.updatePermission(admin, root.getId(), permissions);

        assertThat(newPermissions).isEqualToIgnoringGivenFields(permissions, "id", "creationTime", "lastModifiedTime");
    }

    /**
     * Test if path display works correctly.
     */
    @Test
    void buildDirectoryPathTest() throws MopsException {
        String subDirName1 = "a";
        String subDirName2 = "b";

        Directory createdFirstDir = directoryService.createFolder(admin, root.getId(), subDirName1);
        Directory createdSecondDir = directoryService.createFolder(admin, createdFirstDir.getId(), subDirName2);

        List<Directory> shouldReturn = List.of(root, createdFirstDir, createdSecondDir);
        List<Directory> result = directoryService.getDirectoryPath(createdSecondDir.getId());
        assertThat(result).containsSequence(shouldReturn);
    }

    /**
     * Test if path display works correctly.
     */
    @Test
    void buildDirectoryPathInRootTest() throws MopsException {
        List<Directory> result = directoryService.getDirectoryPath(root.getId());
        assertThat(result).containsExactly(root);
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
                .entry(VIEWER, false, false, false)
                .entry(EDITOR, true, false, false)
                .entry(ADMIN, true, true, true)
                .build();

        directoryService.updatePermission(admin, root.getId(), permissions);

        assertThatExceptionOfType(ReadAccessPermissionException.class)
                .isThrownBy(() -> directoryService.getSubFolders(user, root.getId()));
    }

    @Test
    void searchFolderTest() throws MopsException {
        FileQuery query = FileQuery.builder()
                .name("a")
                .build();

        FileInfo matchingFile1 = fileInfoRepository.save(
                FileInfo.builder()
                        .name("a")
                        .directory(root)
                        .type("txt")
                        .size(0L)
                        .owner(VIEWER)
                        .build()
        );

        Directory child = directoryService.createFolder(admin, root.getId(), "child");

        FileInfo matchingFile2 = fileInfoRepository.save(
                FileInfo.builder()
                        .name("ab")
                        .directory(child)
                        .type("txt")
                        .size(0L)
                        .owner(VIEWER)
                        .build()
        );

        fileInfoRepository.save(
                FileInfo.builder()
                        .name("b")
                        .directory(root)
                        .type("txt")
                        .size(0L)
                        .owner(VIEWER)
                        .build()
        );

        List<FileInfo> fileInfos = directoryService.searchFolder(user, root.getId(), query);

        assertThat(fileInfos).containsExactlyInAnyOrder(matchingFile1, matchingFile2);
    }

    @Test
    void searchFolderWithoutPermissionTest() {
        FileQuery fileQuery = FileQuery.builder()
                .build();

        assertThatExceptionOfType(ReadAccessPermissionException.class)
                .isThrownBy(() -> directoryService.searchFolder(intruder, root.getId(), fileQuery));
    }

    @Test
    void renameAFolder() throws MopsException {
        Directory directory = Directory.builder()
                .fromParent(root)
                .name("oldDirName")
                .build();

        directory = directoryService.saveDirectory(directory);

        directoryService.renameDirectory(admin, directory.getId(), "new Dir-Name/");
        Directory newNamedDir = directoryService.getDirectory(directory.getId());
        assertThat(newNamedDir.getName()).isEqualTo("new_Dir-Name_");
    }

    @Test
    void emptyName() {
        assertThatThrownBy(() -> {
            directoryService.renameDirectory(admin, 1, "   ");
        }).isInstanceOf(EmptyNameException.class);
    }

    @Test
    void renamePermissions() throws MopsException {
        Directory directory = Directory.builder()
                .fromParent(root)
                .name("oldDirName")
                .build();

        long dirId = directoryService.saveDirectory(directory).getId();

        assertThatThrownBy(() -> {
            directoryService.renameDirectory(editor, dirId, "something random");
        }).isInstanceOf(WriteAccessPermissionException.class);

        assertThatThrownBy(() -> {
            directoryService.renameDirectory(user, dirId, "something random");
        }).isInstanceOf(WriteAccessPermissionException.class);

        assertThatCode(() -> {
            directoryService.renameDirectory(admin, dirId, "something random");
        }).doesNotThrowAnyException();
    }
}
