package mops.businesslogic.directory;

import mops.businesslogic.file.FileContainer;
import mops.businesslogic.file.FileListEntry;
import mops.businesslogic.file.FileService;
import mops.businesslogic.security.Account;
import mops.businesslogic.security.UserPermission;
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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ZipServiceTest {

    @Mock
    DirectoryService directoryService;

    @Mock
    FileService fileService;

    ZipService zipService;
    Account account;
    long groupOwner;
    long permissionsId;
    Resource content;

    @BeforeEach
    void setup() {
        zipService = new ZipServiceImpl(directoryService, fileService);
        account = Account.of("Fridolin", "fridolin@pinguin.de", "admin");
        groupOwner = 2L;
        permissionsId = 4L;

        String path = "static/root/test_image.jpg";
        content = new ClassPathResource(path);
    }

    @Test
    void zipDirectoryWithOneFileTest() throws MopsException, IOException {
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
        FileListEntry fileListEntry = new FileListEntry(
                fileInfo,
                new UserPermission(true, false, false),
                false,
                false,
                LocalDateTime.of(2020, 1, 1, 0, 0).toInstant(ZoneOffset.UTC)
        );
        FileContainer fileContainer = new FileContainer(fileInfo, content);

        try (ZipInputStream expectedInputStream =
                     new ZipInputStream(new BufferedInputStream(Files.newInputStream(
                             Path.of("src/test/resources/static/root.zip"))))) {
            given(directoryService.getDirectory(dirId)).willReturn(directory);
            given(fileService.getFilesOfDirectory(any(Account.class), eq(dirId)))
                    .willReturn(List.of(fileListEntry));
            given(fileService.getFile(account, fileInfo.getId())).willReturn(fileContainer);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            zipService.zipDirectory(account, dirId, bos);

            try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(bos.toByteArray()))) {
                ZipEntry nextEntry;
                while ((nextEntry = zipInputStream.getNextEntry()) != null) {
                    ZipEntry expectedNextEntry = expectedInputStream.getNextEntry();
                    assertThat(nextEntry.getName()).isEqualTo(expectedNextEntry.getName());
                    assertThat(nextEntry.isDirectory()).isEqualTo(expectedNextEntry.isDirectory());
                    assertThat(zipInputStream.readAllBytes()).isEqualTo(expectedInputStream.readAllBytes());
                }
            }
        }
    }

    @Test
    void zipDirectoryWithNestedDirectoriesTest() throws IOException, MopsException {
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
        FileListEntry fileListEntry = new FileListEntry(
                fileInfo,
                new UserPermission(true, false, false),
                false,
                false,
                LocalDateTime.of(2020, 1, 1, 0, 0).toInstant(ZoneOffset.UTC)
        );
        FileListEntry fileListEntryCopy = new FileListEntry(
                fileInfoCopy,
                new UserPermission(true, false, false),
                false,
                false,
                LocalDateTime.of(2020, 1, 1, 0, 0).toInstant(ZoneOffset.UTC)
        );
        FileContainer fileContainer = new FileContainer(fileInfo, content);

        given(directoryService.getDirectory(deepDirId)).willReturn(deepDir);

        given(fileService.getFilesOfDirectory(account, deepDirId)).willReturn(List.of(fileListEntry));
        given(fileService.getFilesOfDirectory(account, bottomDirId)).willReturn(List.of(fileListEntryCopy));

        given(fileService.getFile(eq(account), anyLong())).willReturn(fileContainer);

        given(directoryService.getSubFolders(account, deepDirId)).willReturn(List.of(bottom));


        try (ZipInputStream expectedInputStream =
                     new ZipInputStream(new BufferedInputStream(Files.newInputStream(
                             Path.of("src/test/resources/static/deepZip.zip"))))) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            zipService.zipDirectory(account, deepDirId, bos);

            try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(bos.toByteArray()))) {
                ZipEntry nextEntry;
                while ((nextEntry = zipInputStream.getNextEntry()) != null) {
                    ZipEntry expectedNextEntry = expectedInputStream.getNextEntry();
                    assertThat(nextEntry.getName()).isEqualTo(expectedNextEntry.getName());
                    assertThat(nextEntry.isDirectory()).isEqualTo(expectedNextEntry.isDirectory());
                    assertThat(zipInputStream.readAllBytes()).isEqualTo(expectedInputStream.readAllBytes());
                }
            }
        }
    }
}
