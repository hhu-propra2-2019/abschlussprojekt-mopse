package mops;

import mops.persistence.FileRepository;
import mops.util.DbContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@DbContext
@SpringBootTest
class Material1ApplicationTests {

    @MockBean
    FileRepository fileRepository;

    @Test
    void contextLoads() {
    }
}
