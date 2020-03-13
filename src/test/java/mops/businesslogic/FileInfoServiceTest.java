package mops.businesslogic;

import mops.persistence.FileInfoRepository;
import mops.persistence.FileRepository;
import mops.utils.TestContext;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@TestContext
@SpringBootTest
public class FileInfoServiceTest {

    @MockBean
    FileInfoRepository fileInfoRepository;
    @MockBean
    GroupService groupService;
    @MockBean
    FileRepository fileRepository;
    @MockBean
    FileInfoService fileInfoService;
    @MockBean
    FileService fileService;
    @MockBean
    PermissionService permissionService;
    
    void fetchAllFilesInDirectory() {

    }

    void fetchFileInfo() {

    }

    void saveFileInfo() {

    }

    void deleteFileInfo() {

    }

}
