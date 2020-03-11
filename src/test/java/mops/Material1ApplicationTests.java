package mops;

import mops.businesslogic.FileInfoService;
import mops.businesslogic.FileService;
import mops.businesslogic.GroupService;
import mops.businesslogic.PermissionService;
import mops.persistence.FileRepository;
import mops.security.PermissionService;
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
     * Necessary mock until FileInfoService is implemented.
     */
    @MockBean
    private FileInfoService fileInfoService;

    /**
     * Necessary mock until FileService is implemented.
     */
    @MockBean
    private FileService fileService;

    /**
     * Necessary mock until PermissionService is implemented.
     */
    @MockBean
    private PermissionService permissionService;

    @Test
    void contextLoads() {
    }
}
