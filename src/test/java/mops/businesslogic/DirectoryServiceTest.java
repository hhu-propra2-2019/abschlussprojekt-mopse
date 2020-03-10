package mops.businesslogic;

import mops.SpringTestContext;
import mops.persistence.directory.Directory;
import mops.persistence.file.FileInfo;
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
     * Service for communication related to directories.
     */
    @Autowired
    private DirectoryService directoryService;

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
     * Id of the permission object for that folder.
     */
    private long permissionsId;
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
        permissionsId = 1L;
    }

    /**
     * Test if sub folders are correctly returned.
     */
    @Test
    public void getSubFoldersTest() {
        String nameFirstDirectory = "first";
        String nameSecondDirectory = "second";

        Directory firstDirectory = new Directory(nameFirstDirectory, parentId, groupOwner, permissionsId);
        Directory secondDirectory = new Directory(nameSecondDirectory, parentId, groupOwner, permissionsId);

        directoryService.createFolder(account, parentId, nameFirstDirectory);
        directoryService.createFolder(account, parentId, nameSecondDirectory);

        List<Directory> subFolders = directoryService.getSubFolders(account, parentId);


        assertThat(subFolders).containsExactlyInAnyOrder(firstDirectory, secondDirectory);
    }

    /**
     * Test if returned.
     */
    @Test
    public void uploadFileTest() {
        Directory fisrtDirectory = new Directory("first", parentId, groupOwner, permissionsId);
        FileInfo fileInfo1 = new FileInfo(multipartFile.getName(), fisrtDirectory.getId(), multipartFile.getContentType(), multipartFile.getSize(), account.getName(), Set.of());

        FileInfo fileInfo2 = directoryService.uploadFile(account, fisrtDirectory.getId(), multipartFile);


        assertThat(fileInfo2).isEqualTo(fileInfo1);
    }
}
