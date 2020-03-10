package mops.businesslogic;

import mops.SpringTestContext;
import mops.persistence.DirectoryPermissionsRepository;
import mops.persistence.directory.Directory;
import mops.persistence.file.FileInfo;
import mops.persistence.permission.DirectoryPermissions;
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
    /**
     * Necessary bean, must be removed when file service is implemented.
     */
    @MockBean
    private FileService fileService;

    /**
     * Necessary bean, must be removed when group service is implemented.
     */
    @MockBean
    private GroupService groupService;

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
        account = new Account("user", "user@hhu.de", Set.of(STUDENTIN));
        admin = new Account("admin", "admin@hhu.de", Set.of(ADMINISTRATOR));
        parentId = 1L;
        groupOwner = 1L;

        given(permissionService.fetchRoleForUserInGroup(eq(admin), anyLong())).willReturn(ADMINISTRATOR);
        given(permissionService.fetchRolesInGroup(anyLong())).willReturn(Set.of(ADMINISTRATOR, STUDENTIN));
        given(permissionService.fetchRoleForUserInDirectory(eq(account), any(Directory.class))).willReturn(STUDENTIN);
        given(permissionService.fetchRoleForUserInDirectory(eq(intruder), any(Directory.class))).willReturn("intruder");
        intruder = new Account("intruder", "intruder@uni-koeln.de", Set.of("intruder"));
    }

    /**
     * Test if a group folder is correctly created.
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
     * Test if sub folders are correctly returned.
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

    @Test
    public void getSubFoldersWithoutPermissionTest() throws WriteAccessPermission {
        Directory root = directoryService.createRootFolder(admin, groupOwner);
        parentId = root.getId();

        assertThatExceptionOfType(ReadAccessPermission.class).isThrownBy(() -> directoryService.getSubFolders(intruder, parentId));
    }

    /**
     * Test if returned.
     */
    @Test
    public void uploadFileTest() {
        long permissionsId = directoryPermissionsRepository.save(new DirectoryPermissions()).getId();

        Directory fisrtDirectory = new Directory("first", parentId, groupOwner, permissionsId);
        FileInfo fileInfo1 = new FileInfo(multipartFile.getName(), fisrtDirectory.getId(),
                multipartFile.getContentType(), multipartFile.getSize(), account.getName(), Set.of());

        FileInfo fileInfo2 = directoryService.uploadFile(account, fisrtDirectory.getId(), multipartFile, Set.of());


        assertThat(fileInfo2).isEqualTo(fileInfo1);
    }
}
