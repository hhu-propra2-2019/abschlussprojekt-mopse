package mops.businesslogic.directory;

import mops.businesslogic.file.FileContainer;
import mops.businesslogic.file.FileService;
import mops.businesslogic.security.Account;
import mops.exception.MopsException;
import mops.persistence.directory.Directory;
import mops.persistence.file.FileInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ZipServiceImplTest {

    private final String generatedZip = "root.zip";
    @Mock
    DirectoryService directoryService;

    @Mock
    FileService fileService;

    private ZipService zipService;
    private Account account;
    private long groupOwner;
    private long permissionsId;
    private Resource content;

    @BeforeEach
    void setUp() {
        zipService = new ZipServiceImpl(directoryService, fileService);
        account = Account.of("Fridolin", "fridolin@pinguin.de", "admin");
        groupOwner = 2L;
        permissionsId = 4L;

        String path = "static/root/test_image.jpg";
        content = new ClassPathResource(path);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get(generatedZip));
    }

    @Test
    public void zipDirectoryWithOneFileTest() throws MopsException, IOException {
        long dirId = 3L;
        long fileId = 1L;


        Directory directory = Directory.builder()
                .name("root")
                .groupOwner(groupOwner)
                .permissions(permissionsId)
                .build();

        FileInfo fileInfo = FileInfo.builder()
                .id(fileId)
                .name("test_image.jpg")
                .directory(dirId)
                .type(MediaType.IMAGE_JPEG_VALUE)
                .size(192_511)
                .owner("Fridolin")
                .build();

        FileContainer file = new FileContainer(fileInfo, content);

        FileInputStream fileInputStream = new FileInputStream("src/test/resources/static/root.zip");
        ZipInputStream expectedInputStream = new ZipInputStream(new BufferedInputStream(fileInputStream));

        given(directoryService.getDirectory(dirId)).willReturn(directory);
        given(fileService.getFilesOfDirectory(any(Account.class), eq(dirId))).willReturn(List.of(fileInfo));
        given(fileService.getFile(account, fileInfo.getId())).willReturn(file);

        ZipOutputStream zipStream = zipService.zipDirectory(account, dirId);
        zipStream.close();
        FileInputStream reloadedStream = new FileInputStream(generatedZip);
        ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(reloadedStream));

        ZipEntry nextEntry;
        while ( (nextEntry = zipInputStream.getNextEntry() ) != null) {
            assertThat(nextEntry).isEqualToComparingOnlyGivenFields(expectedInputStream.getNextEntry(), "name");
        }
    }

    @Test
    public void zipDirectoryWithNestedDirectoriesTest() throws IOException, MopsException {
        long deepDirId = 69L;
        long bottomDirId = 42L;
        long fileId = 1L;

        Directory deepDir = Directory.builder()
                .id(deepDirId)
                .name("deepZip")
                .groupOwner(groupOwner)
                .permissions(permissionsId)
                .build();

        Directory bottom = Directory.builder()
                .id(bottomDirId)
                .name("bottom")
                .permissions(permissionsId)
                .groupOwner(groupOwner)
                .build();

        FileInfo fileInfo = FileInfo.builder()
                .id(fileId)
                .name("test_image.jpg")
                .directory(deepDirId)
                .type(MediaType.IMAGE_JPEG_VALUE)
                .size(192_511)
                .owner("Fridolin")
                .build();
        FileInfo fileInfoCopy = FileInfo.builder().from(fileInfo).directory(bottomDirId).build();


        FileContainer file = new FileContainer(fileInfo, content);

        given(directoryService.getDirectory(deepDirId)).willReturn(deepDir);

        given(fileService.getFilesOfDirectory(account, deepDirId)).willReturn(List.of(fileInfo));
        given(fileService.getFilesOfDirectory(account, bottomDirId)).willReturn(List.of(fileInfoCopy));

        given(fileService.getFile(eq(account), anyLong())).willReturn(file);

        given(directoryService.getSubFolders(account, deepDirId)).willReturn(List.of(bottom));



        FileInputStream fileInputStream = new FileInputStream("src/test/resources/static/deepZip.zip");
        ZipInputStream expectedInputStream = new ZipInputStream(new BufferedInputStream(fileInputStream));


        ZipOutputStream zipStream = zipService.zipDirectory(account, deepDirId);
        zipStream.close();
        FileInputStream reloadedStream = new FileInputStream("deepZip.zip");
        ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(reloadedStream));

        ZipEntry nextEntry;
        while ( (nextEntry = zipInputStream.getNextEntry() ) != null) {
            assertThat(nextEntry).isEqualToComparingOnlyGivenFields(expectedInputStream.getNextEntry(), "name");
        }
    }
}