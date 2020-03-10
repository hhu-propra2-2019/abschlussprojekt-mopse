package mops.businesslogic;

import mops.SpringTestContext;
import mops.persistence.DirectoryPermissionsRepository;
import mops.persistence.directory.Directory;
import mops.persistence.file.FileInfo;
import mops.persistence.permission.DirectoryPermissions;
import mops.security.PermissionService;
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

@SpringTestContext
@SpringBootTest
public class DirectoryServiceTest {
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
     * Account Object containing user credentials.
     */
    private Account account;
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
        account = new Account("user", "user@hhu.de", Set.of("studentin"));
        parentId = 1L;
        groupOwner = 1L;
    }

    /**
     * Test if a group folder is correctly created.
     */
    @Test
    public void createGroupRootFolder() {
        String nameFirstDirectory = String.valueOf(groupOwner);
        long permissionsId = directoryPermissionsRepository.save(new DirectoryPermissions()).getId();
        Directory expectedDirectory = new Directory(1L, nameFirstDirectory, null, groupOwner, permissionsId + 1L);

        Directory directory = directoryService.createRootFolder(account, groupOwner);

        assertThat(directory).isEqualTo(expectedDirectory);
    }

    /**
     * Test if folder is created in a given root folder.
     */
    @Test
    public void createFolderTest() {
        Directory root = directoryService.createRootFolder(account, groupOwner);
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
    public void getSubFoldersTest() {
        Directory root = directoryService.createRootFolder(account, groupOwner);
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
