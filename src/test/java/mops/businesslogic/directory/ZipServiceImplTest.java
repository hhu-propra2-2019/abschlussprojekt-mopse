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

import java.io.FileOutputStream;
import java.io.IOException;
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
    private Account account;

    @BeforeEach
    void setUp() {
        zipService = new ZipServiceImpl(directoryService, fileService);
        account = Account.of("Fridolin", "fridolin@pinguin.de", "admin");
    }

    @Test
    public void zipDirectoryWithOneFileTest() throws MopsException, IOException {
        long dirId = 3L;
        long groupOwner = 2L;
        long permissionsId = 4L;
        long fileId = 1L;


        Directory directory = Directory.builder()
                .name("root")
                .groupOwner(groupOwner)
                .permissions(permissionsId)
                .build();

        FileInfo fileInfo = FileInfo.builder()
                .id(fileId)
                .name("test_image")
                .directory(dirId)
                .type(MediaType.IMAGE_JPEG_VALUE)
                .size(192_511)
                .owner("Fridolin")
                .build();

        String path = "static/test_image.jpg";
        Resource content = new ClassPathResource(path);
        FileContainer file = new FileContainer(fileInfo, content);

        FileOutputStream fileOutputStream = new FileOutputStream(directory.getName());
        ZipOutputStream expectedZipStream = new ZipOutputStream(fileOutputStream);

        given(directoryService.getDirectory(dirId)).willReturn(directory);
        given(fileService.getFilesOfDirectory(any(Account.class), eq(dirId))).willReturn(List.of(fileInfo));
        given(fileService.getFile(account, fileInfo.getId())).willReturn(file);


        ZipOutputStream zipStream = zipService.zipDirectory(account, dirId);

        assertThat(zipStream).isEqualTo(expectedZipStream);
    }
}