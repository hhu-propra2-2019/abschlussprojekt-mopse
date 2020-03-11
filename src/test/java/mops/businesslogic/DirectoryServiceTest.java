package mops.businesslogic;

import mops.SpringTestContext;
import mops.persistence.DirectoryPermissionsRepository;
import mops.persistence.directory.Directory;
import mops.persistence.permission.DirectoryPermissionEntry;
import mops.persistence.permission.DirectoryPermissions;
import mops.security.DeleteAccessPermission;
import mops.security.PermissionService;
import mops.security.ReadAccessPermission;
import mops.security.exception.WriteAccessPermission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@SpringTestContext
@SpringBootTest
public class DirectoryServiceTest {
    public static final String ADMINISTRATOR = "administrator";
    public static final String STUDENTIN = "studentin";
    public static final String READER = "reader";
    public static final String INTRUDER = "intruder";
    public static final String USER = "user";

    /**
     * Necessary bean, must be removed when group service is implemented.
     */
    @MockBean
    private GroupService groupService;

    /**
     * Necessary bean, must be removed when file info service is implemented.
     */
    @MockBean
    private FileInfoService fileInfoService;

    /**
     * API for getting permission roles from GruppenFindung.
     */
    @MockBean
    private PermissionService permissionService;

    /**
     * Service for communication related to directories.
     */
    @Autowired
    private DirectoryService directoryService;

    /**
     * Repository for directory permissions.
     */
    @Autowired
    private DirectoryPermissionsRepository directoryPermissionsRepository;

    /**
     * Account Object containing user credentials with admin role.
     */
    private Account admin;

    /**
     * Account Object containing user credentials.
     */
    private Account account;

    /**
     * An Account with no rights what so ever.
     */
    private Account intruder;

    /**
     * An Account with read-only rights.
     */
    private Account reader;


    /**
     * Id of the parent folder.
     */
    private long parentId;
    /**
     * Id of the group owner.
     */
    private long groupOwner;

    /**
     * MultipartFile Object containing File credentials.
     */
    @Mock
    private MultipartFile multipartFile;


    /**
     * Creates a user account.
     */
    @BeforeEach
    void setUp() {
        account = new Account(USER, "user@hhu.de", Set.of(STUDENTIN));
        admin = new Account(ADMINISTRATOR, "admin@hhu.de", Set.of(ADMINISTRATOR));
        intruder = new Account(INTRUDER, "intruder@uni-koeln.de", Set.of(INTRUDER));
        reader = new Account(READER, "reader@hhu.de", Set.of(STUDENTIN));
        parentId = 1L;
        groupOwner = 1L;

        given(permissionService.fetchRoleForUserInGroup(eq(admin), anyLong())).willReturn(ADMINISTRATOR);
        given(permissionService.fetchRolesInGroup(anyLong())).willReturn(Set.of(ADMINISTRATOR, STUDENTIN));
        given(permissionService.fetchRoleForUserInDirectory(eq(admin), any(Directory.class))).willReturn(ADMINISTRATOR);
        given(permissionService.fetchRoleForUserInDirectory(eq(account), any(Directory.class))).willReturn(STUDENTIN);
        given(permissionService.fetchRoleForUserInDirectory(eq(intruder), any(Directory.class))).willReturn(INTRUDER);
        given(permissionService.fetchRoleForUserInDirectory(eq(reader), any(Directory.class))).willReturn(READER);
        given(fileInfoService.fetchAllFilesInDirectory(anyLong())).willReturn(List.of());
    }

    /**
     * Test if a group folder is correctly created.
     *
     * @throws WriteAccessPermission user does not have writing permission
     */
    @Test
    public void createGroupRootFolder() throws WriteAccessPermission {
        String nameFirstDirectory = String.valueOf(groupOwner);
        long permissionsId = directoryPermissionsRepository.save(new DirectoryPermissions()).getId();
        Directory expectedDirectory = new Directory(nameFirstDirectory, null, groupOwner, permissionsId + 1L);

        Directory directory = directoryService.createRootFolder(admin, groupOwner);

        assertThat(directory).isEqualToIgnoringGivenFields(expectedDirectory, "id");
    }

    /**
     * Test if a group folder is not created when the user does not have permission.
     */
    @Test
    public void createGroupRootFolderWithoutPermission() {
        assertThatExceptionOfType(WriteAccessPermission.class).isThrownBy(() -> directoryService.createRootFolder(account, groupOwner));
    }

    /**
     * Test if folder is created in a given root folder.
     *
     * @throws WriteAccessPermission user does not have writing permission
     */
    @Test
    public void createFolderTest() throws WriteAccessPermission {
        Directory root = directoryService.createRootFolder(admin, groupOwner);
        parentId = root.getId();
        long permissionsId = root.getPermissionsId();
        String nameFirstDirectory = "first";

        Directory expectedDirectory = new Directory(parentId + 1,
                nameFirstDirectory,
                parentId,
                groupOwner,
                permissionsId);

        Directory folder = directoryService.createFolder(account, parentId, nameFirstDirectory);

        assertThat(folder).isEqualTo(expectedDirectory);
    }

    /**
     * Test if admin can update the permissions of a directory.
     *
     * @throws WriteAccessPermission user does not have write permission
     */
    @Test
    public void updatePermissionTest() throws WriteAccessPermission {
        Directory root = directoryService.createRootFolder(admin, groupOwner);
        Long groupId = root.getId();

        DirectoryPermissionEntry readerEntry = new DirectoryPermissionEntry(READER, false, true, false);
        DirectoryPermissionEntry adminEntry = new DirectoryPermissionEntry(ADMINISTRATOR, true, true, true);
        Set<DirectoryPermissionEntry> permissionEntries = Set.of(adminEntry, readerEntry);

        Directory directory = directoryService.updatePermission(admin, groupId, permissionEntries);

        assertThat(directory).isEqualTo(root);
    }

    /**
     * Test if sub folders are correctly returned.
     *
     * @throws WriteAccessPermission user does not have writing permission
     * @throws ReadAccessPermission  user does not have reading permission
     */
    @Test
    public void getSubFoldersTest() throws WriteAccessPermission, ReadAccessPermission {
        Directory root = directoryService.createRootFolder(admin, groupOwner);
        parentId = root.getId();
        long permissionsId = root.getPermissionsId();

        String nameFirstDirectory = "first";
        String nameSecondDirectory = "second";

        Directory firstDirectory = new Directory(nameFirstDirectory, parentId, groupOwner, permissionsId);
        Directory secondDirectory = new Directory(nameSecondDirectory, parentId, groupOwner, permissionsId);

        directoryService.createFolder(account, parentId, nameFirstDirectory);
        directoryService.createFolder(account, parentId, nameSecondDirectory);

        List<Directory> subFolders = directoryService.getSubFolders(account, parentId);

        firstDirectory.setId(root.getId() + 1L);
        secondDirectory.setId(root.getId() + 2L);

        assertThat(subFolders).containsExactlyInAnyOrder(firstDirectory, secondDirectory);
    }

    /**
     * Checks if exception is thrown if the user does not have reading permission.
     *
     * @throws WriteAccessPermission user does not have writing permission
     */
    @Test
    public void getSubFoldersWithoutPermissionTest() throws WriteAccessPermission {
        Directory root = directoryService.createRootFolder(admin, groupOwner);
        parentId = root.getId();

        assertThatExceptionOfType(ReadAccessPermission.class).isThrownBy(() -> directoryService.getSubFolders(intruder, parentId));
    }

    /**
     * Test if a user with read only permission can't create a sub folder.
     *
     * @throws WriteAccessPermission user does not have writing permissions
     */
    @Test
    public void createSubFolderWithReadsOnlyPermissionTest() throws WriteAccessPermission {
        Directory root = directoryService.createRootFolder(admin, groupOwner);
        Long parentId = root.getId();
        DirectoryPermissionEntry readerEntry = new DirectoryPermissionEntry(READER, false, true, false);
        DirectoryPermissionEntry adminEntry = new DirectoryPermissionEntry(ADMINISTRATOR, true, true, true);
        Set<DirectoryPermissionEntry> permissionEntries = Set.of(adminEntry, readerEntry);
        directoryService.updatePermission(admin, parentId, permissionEntries);
        assertThatExceptionOfType(ReadAccessPermission.class).isThrownBy(() -> directoryService.getSubFolders(reader, parentId));
    }

    /**
     * Tests if a admin can delete subfolder.
     */
    @Test
    public void deleteSubFolderTest() throws WriteAccessPermission, DeleteAccessPermission, ReadAccessPermission {
        Directory root = directoryService.createRootFolder(admin, groupOwner);
        Directory subFolder = directoryService.createFolder(account, root.getId(), "subFolder");
        Directory directory = directoryService.deleteFolder(admin, subFolder.getId());

        assertThat(directory).isEqualTo(root);
    }

    /**
     * Checks if exception is thrown if the user does not have writing permission.
     */
    @Test
    public void checkWritePermission() throws WriteAccessPermission{
        Directory root = directoryService.createRootFolder(admin, groupOwner);
        parentId = root.getId();

        assertThatExceptionOfType(WriteAccessPermission.class).isThrownBy(() ->
                directoryService.checkWritePermission(intruder, parentId));
    }
}
