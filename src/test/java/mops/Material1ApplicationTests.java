package mops;

import mops.businesslogic.DirectoryService;
import mops.businesslogic.FileInfoService;
import mops.businesslogic.GroupService;
import mops.businesslogic.PermissionService;
import mops.persistence.FileRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringTestContext
@SpringBootTest
class Material1ApplicationTests {

    /**
     * The server is not available while testing.
     */
    @MockBean
    private FileRepository fileRepository;
    /**
     * Necessary mock until GroupService is implemented.
     */
    @MockBean
    private GroupService groupService;

    /**
     * Necessary mock until DirectoryService is implemented.
     */
    @MockBean
    private DirectoryService directoryService;

    /**
     * Necessary mock until FileService is implemented.
     */
    @MockBean
    private FileInfoService fileInfoService;

    @Test
    void contextLoads() {
    }
}
