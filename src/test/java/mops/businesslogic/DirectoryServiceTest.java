package mops.businesslogic;

import mops.SpringTestContext;
import mops.businesslogic.exception.DeleteAccessPermissionException;
import mops.businesslogic.exception.ReadAccessPermissionException;
import mops.businesslogic.exception.WriteAccessPermissionException;
import mops.exception.MopsException;
import mops.persistence.DirectoryPermissionsRepository;
import mops.persistence.FileRepository;
import mops.persistence.directory.Directory;
import mops.persistence.permission.DirectoryPermissionEntry;
import mops.persistence.permission.DirectoryPermissions;
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
    @MockBean
    private FileRepository fileRepository;

    /**
     * Necessary bean, must be removed when file info service is implemented.
     */
    @MockBean
    private FileInfoService fileInfoService;
    /**
     * Necessary bean, must be removed when file service is implemented.
     */
    @MockBean
    private FileService fileService;

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
        account = Account.of(USER, "user@hhu.de", Set.of(STUDENTIN));
        admin = Account.of(ADMINISTRATOR, "admin@hhu.de", Set.of(ADMINISTRATOR));
        intruder = Account.of(INTRUDER, "intruder@uni-koeln.de", Set.of(INTRUDER));
        reader = Account.of(READER, "reader@hhu.de", Set.of(STUDENTIN));
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
     */
    @Test
    public void createGroupRootFolder() throws MopsException {
        String nameFirstDirectory = String.valueOf(groupOwner);
        long permissionsId = directoryPermissionsRepository.save(new DirectoryPermissions(Set.of())).getId();
        Directory expectedDirectory = new Directory(nameFirstDirectory, null, groupOwner, permissionsId + 1L);

        Directory directory = directoryService.createRootFolder(admin, groupOwner);

        assertThat(directory).isEqualToIgnoringGivenFields(expectedDirectory, "id", "creationTime", "lastModifiedTime");
    }

    /**
     * Test if a group folder is not created when the user does not have permission.
     */
    @Test
    public void createGroupRootFolderWithoutPermission() {
        assertThatExceptionOfType(WriteAccessPermissionException.class).isThrownBy(() -> directoryService.createRootFolder(account, groupOwner));
    }

    /**
     * Test if folder is created in a given root folder.
     */
    @Test
    public void createFolderTest() throws MopsException {
        Directory root = directoryService.createRootFolder(admin, groupOwner);
        parentId = root.getId();
        long permissionsId = root.getPermissionsId();
        String nameFirstDirectory = "first";

        Directory expectedDirectory = new Directory(
                nameFirstDirectory,
                parentId,
                groupOwner,
                permissionsId);

        Directory folder = directoryService.createFolder(account, parentId, nameFirstDirectory);

        assertThat(folder).isEqualToIgnoringGivenFields(expectedDirectory, "id", "creationTime", "lastModifiedTime");
    }

    /**
     * Test if admin can update the permissions of a directory.
     */
    @Test
    public void updatePermissionTest() throws MopsException {
        Directory root = directoryService.createRootFolder(admin, groupOwner);
        Long groupId = root.getId();

        DirectoryPermissionEntry readerEntry = new DirectoryPermissionEntry(READER, false, true, false);
        DirectoryPermissionEntry adminEntry = new DirectoryPermissionEntry(ADMINISTRATOR, true, true, true);
        Set<DirectoryPermissionEntry> permissionEntries = Set.of(adminEntry, readerEntry);

        Directory directory = directoryService.updatePermission(admin, groupId, permissionEntries);

        assertThat(directory).isEqualToIgnoringGivenFields(root, "creationTime", "lastModifiedTime");
    }

    /**
     * Test if sub folders are correctly returned.
     */
    @Test
    public void getSubFoldersTest() throws MopsException {
        Directory root = directoryService.createRootFolder(admin, groupOwner);
        parentId = root.getId();

        String nameFirstDirectory = "first";
        String nameSecondDirectory = "second";

        Directory createdFirstDir = directoryService.createFolder(account, parentId, nameFirstDirectory);
        Directory createdSecondDir = directoryService.createFolder(account, parentId, nameSecondDirectory);

        List<Directory> subFolders = directoryService.getSubFolders(account, parentId);

        assertThat(subFolders).containsExactlyInAnyOrder(createdFirstDir, createdSecondDir);
    }

    /**
     * Checks if exception is thrown if the user does not have reading permission.
     */
    @Test
    public void getSubFoldersWithoutPermissionTest() throws MopsException {
        Directory root = directoryService.createRootFolder(admin, groupOwner);
        parentId = root.getId();

        assertThatExceptionOfType(ReadAccessPermissionException.class).isThrownBy(() -> directoryService.getSubFolders(intruder, parentId));
    }

    /**
     * Test if a user with read only permission can't create a sub folder.
     */
    @Test
    public void createSubFolderWithReadsOnlyPermissionTest() throws MopsException {
        Directory root = directoryService.createRootFolder(admin, groupOwner);
        Long parentId = root.getId();
        DirectoryPermissionEntry readerEntry = new DirectoryPermissionEntry(READER, false, true, false);
        DirectoryPermissionEntry adminEntry = new DirectoryPermissionEntry(ADMINISTRATOR, true, true, true);
        Set<DirectoryPermissionEntry> permissionEntries = Set.of(adminEntry, readerEntry);
        directoryService.updatePermission(admin, parentId, permissionEntries);
        assertThatExceptionOfType(ReadAccessPermissionException.class).isThrownBy(() -> directoryService.getSubFolders(reader, parentId));
    }

    /**
     * Tests if a admin can delete subfolder.
     */
    @Test
    public void deleteSubFolderTest() throws MopsException {
        Directory root = directoryService.createRootFolder(admin, groupOwner);
        Directory subFolder = directoryService.createFolder(account, root.getId(), "subFolder");
        Directory directory = directoryService.deleteFolder(admin, subFolder.getId());

        assertThat(directory).isEqualToIgnoringGivenFields(root, "creationTime", "lastModifiedTime");
    }

    /**
     * Checks if exception is thrown if the user does not have writing permission.
     */
    @Test
    public void checkWritePermission() throws MopsException {
        Directory root = directoryService.createRootFolder(admin, groupOwner);
        parentId = root.getId();

        assertThatExceptionOfType(WriteAccessPermissionException.class).isThrownBy(() ->
                directoryService.checkWritePermission(intruder, parentId));
    }

    /**
     * Checks if exception is thrown if the user does not have reading permission.
     */
    @Test
    public void checkReadPermission() throws MopsException {
        Directory root = directoryService.createRootFolder(admin, groupOwner);
        parentId = root.getId();

        assertThatExceptionOfType(ReadAccessPermissionException.class).isThrownBy(() ->
                directoryService.checkReadPermission(intruder, parentId));
    }

    /**
     * Checks if exception is thrown if the user does not have deleting permission.
     */
    @Test
    public void checkDeletePermission() throws MopsException {
        Directory root = directoryService.createRootFolder(admin, groupOwner);
        parentId = root.getId();

        assertThatExceptionOfType(DeleteAccessPermissionException.class).isThrownBy(() ->
                directoryService.checkDeletePermission(intruder, parentId));
    }
}
