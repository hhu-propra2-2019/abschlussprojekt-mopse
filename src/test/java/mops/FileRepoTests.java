package mops;

import mops.businesslogic.DirectoryService;
import mops.businesslogic.FileService;
import mops.businesslogic.GroupService;
import mops.persistence.FileRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import static org.assertj.core.api.Assertions.assertThat;


import java.util.Random;

@SpringBootTest
public class FileRepoTests {

    @Autowired
    private FileRepositoryImpl fileRepository;

    @MockBean
    DirectoryService directoryService;
    @MockBean
    FileService fileService;
    @MockBean
    GroupService groupService;


    @Test
    public void testRepo() throws Exception {
        assertThat(true).isFalse();
//        fileRepository.FileRepositoryImplSetUp();
        System.out.println("lol?");
        assertThat(false).isTrue();
        byte[] b = new byte[20];
        new Random().nextBytes(b);

        MultipartFile file = new MockMultipartFile("Somefile.txt", b);
        boolean result = fileRepository.saveFile(file, 7L);

        assertThat(result).isTrue();

    }

}
