package mops;

import mops.businesslogic.GroupService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SuppressWarnings("PMD")
@SpringBootTest
class Material1ApplicationTests {
    /**
     * Necessary mock until GroupService is implemented.
     */
    @MockBean
    private GroupService groupService;


    @Test
    void contextLoads() {
    }
}
