package mops.businesslogic;

import mops.SpringTestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringTestContext
@SpringBootTest
public class DirectoryServiceImplTest {
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
}
