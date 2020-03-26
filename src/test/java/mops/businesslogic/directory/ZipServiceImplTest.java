package mops.businesslogic.directory;

import mops.businesslogic.file.FileContainer;
import mops.businesslogic.file.FileService;
import mops.businesslogic.security.Account;
import mops.exception.MopsException;
import mops.persistence.directory.Directory;
import mops.persistence.file.FileInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.zip.ZipOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ZipServiceImplTest {

    @Mock
    DirectoryService directoryService;

    @Mock
    FileService fileService;

    private ZipService zipService;

    @BeforeEach
    void setUp() {
        zipService = new ZipServiceImpl();
    }

    @Test
    public void zipDirectoryWithOneFileTest() throws MopsException, FileNotFoundException {
        long dirId = 3L;
        long groupOwner = 2L;
        long permissionsId = 4L;

        Directory directory = Directory.builder()
                .name("root")
                .groupOwner(groupOwner)
                .permissions(permissionsId)
                .build();

        FileInfo fileInfo = FileInfo.builder()
                .name("test_image")
                .directory(dirId)
                .type(MediaType.IMAGE_JPEG_VALUE)
                .size(192_511)
                .owner("Fridolin")
                .build();

        Resource content = new ClassPathResource("resources/static/test_image.jpg");
        FileContainer file = new FileContainer(fileInfo, content);

        FileOutputStream fileOutputStream = new FileOutputStream(directory.getName());
        ZipOutputStream expectedZipStream = new ZipOutputStream(fileOutputStream);

        given(directoryService.getDirectory(dirId)).willReturn(directory);
        given(fileService.getFilesOfDirectory(any(Account.class), eq(dirId))).willReturn(List.of(fileInfo));


        ZipOutputStream zipStream = zipService.zipDirectory(dirId);

        assertThat(zipStream).isEqualTo(expectedZipStream);
    }
}