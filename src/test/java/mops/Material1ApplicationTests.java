package mops;

import mops.businesslogic.DirectoryService;
import mops.businesslogic.FileService;
import mops.businesslogic.GroupService;
import mops.businesslogic.PermissionService;
import mops.persistence.FileRepository;
import mops.utils.TestContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@TestContext
@SpringBootTest
class Material1ApplicationTests {

    @MockBean
    FileRepository fileRepository;
    @MockBean
    GroupService groupService;
    @MockBean
    FileService fileService;
    @MockBean
    DirectoryService directoryService;
    @MockBean
    PermissionService permissionService;

    @Test
    void contextLoads() {
    }
}
