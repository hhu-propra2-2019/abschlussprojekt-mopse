package mops.businesslogic;

import mops.businesslogic.exception.DeleteAccessPermissionException;
import mops.businesslogic.exception.ReadAccessPermissionException;
import mops.businesslogic.exception.WriteAccessPermissionException;
import mops.exception.MopsException;
import mops.persistence.FileRepository;
import mops.persistence.file.FileInfo;
import org.junit.jupiter.api.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@ExtendWith(MockitoExtension.class)
public class FileServiceTest {

    private FileService fileService;
    private DirectoryService directoryServiceMock;
    private FileInfoService fileInfoServiceMock;
    private FileRepository fileRepositoryMock;
    private Random random;
    private MultipartFile file;
    private Account account;

    @BeforeEach
    void prepareTest() {
        directoryServiceMock = mock(DirectoryService.class);
        fileInfoServiceMock = mock(FileInfoService.class);
        fileRepositoryMock = mock(FileRepository.class);
        fileService = new FileServiceImpl(directoryServiceMock, fileInfoServiceMock, fileRepositoryMock);
    }

    @BeforeAll
    void setUp() {
        random = new Random();
        file = new MockMultipartFile("file.bin",
                "originalFilename",
                "text/plain",
                getRandomBytes()
        );
        account = Account.of("user1234", "mail", new HashSet<>());

    }

    @Test
    void canSaveAFile() throws MopsException {

        Set<String> tags = new HashSet<>();
        long dirId = 1;
        long fileId = 17;

        UserPermission userPermission = new UserPermission(true, true, false);
        doReturn(userPermission)
                .when(directoryServiceMock)
                .getPermissionsOfUser(account, dirId);

        FileInfo fileInfoStub =  FileInfo.builder()
                .from(file)
                .id(fileId)
                .directory(dirId)
                .owner(account.getName())
                .build();

        doReturn(fileInfoStub)
                .when(fileInfoServiceMock)
                .saveFileInfo(any());

        fileService.saveFile(account, dirId, file, tags);

        verify(fileRepositoryMock, times(1)).saveFile(file, fileId);
    }

    @Test
    void NoPermissionToSaveAFile() throws MopsException {
        Set<String> tags = new HashSet<>();
        long dirId = 1;
        long fileId = 17;

        UserPermission userPermission = new UserPermission(true, false, false);
        doReturn(userPermission)
                .when(directoryServiceMock)
                .getPermissionsOfUser(account, dirId);

        FileInfo fileInfoStub =  FileInfo.builder()
                .from(file)
                .id(fileId)
                .directory(dirId)
                .owner(account.getName())
                .build();

        doReturn(fileInfoStub)
                .when(fileInfoServiceMock)
                .saveFileInfo(any());

        assertThrows(WriteAccessPermissionException.class, () -> {
            fileService.saveFile(account, dirId, file, tags);
        });

        verify(fileRepositoryMock, never()).saveFile(file, fileId);
    }

    @Test
    void ownerCanAlwaysDelete() throws MopsException {
        long dirId = 1;
        long fileId = 17;

        // no delete permission
        UserPermission userPermission = new UserPermission(true, false, false);
        doReturn(userPermission)
                .when(directoryServiceMock)
                .getPermissionsOfUser(account, dirId);

        FileInfo fileInfoStub =  FileInfo.builder()
                .from(file)
                .id(fileId)
                .directory(dirId)
                .owner(account.getName()) // account is owner
                .build();

        doReturn(fileInfoStub)
                .when(fileInfoServiceMock)
                .fetchFileInfo(fileId);

        fileService.deleteFile(account, fileId);

        verify(fileInfoServiceMock, times(1)).deleteFileInfo(fileId);
        verify(fileRepositoryMock, times(1)).deleteFile(fileId);
    }

    @Test
    void noDeletePermission() throws MopsException {
        long dirId = 1;
        long fileId = 17;

        // no delete permission
        UserPermission userPermission = new UserPermission(true, false, false);
        doReturn(userPermission)
                .when(directoryServiceMock)
                .getPermissionsOfUser(account, dirId);

        FileInfo fileInfoStub =  FileInfo.builder()
                .from(file)
                .id(fileId)
                .directory(dirId)
                .owner("notUser1234") // account is not the owner
                .build();

        doReturn(fileInfoStub)
                .when(fileInfoServiceMock)
                .fetchFileInfo(fileId);

        assertThrows(DeleteAccessPermissionException.class, () -> {
            fileService.deleteFile(account, fileId);
        });

        verify(fileInfoServiceMock, never()).deleteFileInfo(fileId);
        verify(fileRepositoryMock, never()).deleteFile(fileId);
    }

    @Test
    void notOwnerButDeletePermission() throws MopsException {
        long dirId = 1;
        long fileId = 17;

        UserPermission userPermission = new UserPermission(true, false, true);
        doReturn(userPermission)
                .when(directoryServiceMock)
                .getPermissionsOfUser(account, dirId);

        FileInfo fileInfoStub =  FileInfo.builder()
                .from(file)
                .id(fileId)
                .directory(dirId)
                .owner("notUser1234")
                .build();

        doReturn(fileInfoStub)
                .when(fileInfoServiceMock)
                .fetchFileInfo(fileId);

        fileService.deleteFile(account, fileId);

        verify(fileInfoServiceMock, times(1)).deleteFileInfo(fileId);
        verify(fileRepositoryMock, times(1)).deleteFile(fileId);
    }

    @Test
    void noPermissionToRetrieveFile() throws MopsException {
        long dirId = 1;
        long fileId = 17;

        UserPermission userPermission = new UserPermission(false, false, true);
        doReturn(userPermission)
                .when(directoryServiceMock)
                .getPermissionsOfUser(account, dirId);

        FileInfo fileInfoStub =  FileInfo.builder()
                .from(file)
                .id(fileId)
                .directory(dirId)
                .owner("notUser1234") // irrelevant here
                .build();

        doReturn(fileInfoStub)
                .when(fileInfoServiceMock)
                .fetchFileInfo(fileId);

        assertThrows(ReadAccessPermissionException.class, () -> {
            fileService.getFile(account, fileId);
        });

        verify(fileInfoServiceMock, atLeastOnce()).fetchFileInfo(fileId);
        verify(fileRepositoryMock, never()).getFileContent(fileId);
    }

    @Test
    void canRetrieveFile() throws MopsException, IOException {
        long dirId = 1;
        long fileId = 17;

        UserPermission userPermission = new UserPermission(true, false, false);
        doReturn(userPermission)
                .when(directoryServiceMock)
                .getPermissionsOfUser(account, dirId);

        FileInfo fileInfoStub =  FileInfo.builder()
                .from(file)
                .id(fileId)
                .directory(dirId)
                .owner("notUser1234")
                .build();

        doReturn(fileInfoStub)
                .when(fileInfoServiceMock)
                .fetchFileInfo(fileId);

        doReturn(file.getInputStream())
                .when(fileRepositoryMock)
                .getFileContent(fileId);

        FileContainer fileContainer = fileService.getFile(account, fileId);

        verify(fileInfoServiceMock, atLeastOnce()).fetchFileInfo(fileId);
        verify(fileRepositoryMock, times(1)).getFileContent(fileId);

        byte[] originalContent = file.getBytes();
        byte[] retrievedData = fileContainer.getContent().getInputStream().readAllBytes();

        assertThat(originalContent).isEqualTo(retrievedData);
    }

    private byte[] getRandomBytes() {
        int fileLength = random.nextInt(10000) + 1;
        byte[] bytes = new byte[fileLength];
        random.nextBytes(bytes);
        return bytes;
    }

}
