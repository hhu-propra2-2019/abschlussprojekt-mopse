package mops.businesslogic.file;

import mops.businesslogic.directory.DirectoryService;
import mops.businesslogic.exception.DeleteAccessPermissionException;
import mops.businesslogic.exception.EmptyNameException;
import mops.businesslogic.exception.ReadAccessPermissionException;
import mops.businesslogic.exception.WriteAccessPermissionException;
import mops.businesslogic.security.Account;
import mops.businesslogic.security.SecurityService;
import mops.businesslogic.security.UserPermission;
import mops.businesslogic.time.TimeService;
import mops.exception.MopsException;
import mops.persistence.FileRepository;
import mops.persistence.directory.Directory;
import mops.persistence.file.FileInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Random;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    DirectoryService directoryService;
    @Mock
    FileInfoService fileInfoService;
    @Mock
    SecurityService securityService;
    @Mock
    TimeService timeService;
    @Mock
    FileRepository fileRepository;

    FileService fileService;

    Random random;
    MultipartFile file;
    Account account;

    @BeforeEach
    void prepareTest() {
        random = new Random();
        file = new MockMultipartFile("file.bin",
                "originalFilename.bin",
                "text/plain",
                getRandomBytes()
        );
        account = Account.of("user1234", "mail", Set.of());
        fileService = new FileServiceImpl(directoryService, fileInfoService, securityService, timeService,
                fileRepository);
    }

    @Test
    void canSaveAFile() throws MopsException {
        Set<String> tags = Set.of();
        long dirId = 1;
        long fileId = 17;

        UserPermission userPermission = new UserPermission(true, true, false);
        doReturn(userPermission)
                .when(securityService)
                .getPermissionsOfUser(eq(account), any());

        FileInfo fileInfoStub = FileInfo.builder()
                .from(file)
                .id(fileId)
                .directory(dirId)
                .owner(account.getName())
                .build();

        doReturn(fileInfoStub)
                .when(fileInfoService)
                .saveFileInfo(any());

        fileService.saveFile(account, dirId, file, tags);

        verify(fileRepository, times(1)).saveFile(file, fileId);
    }

    @Test
    void noPermissionToSaveAFile() throws MopsException {
        Set<String> tags = Set.of();
        long dirId = 1;
        long fileId = 17;

        UserPermission userPermission = new UserPermission(true, false, false);
        doReturn(userPermission)
                .when(securityService)
                .getPermissionsOfUser(eq(account), any());

        assertThatThrownBy(() -> {
            fileService.saveFile(account, dirId, file, tags);
        }).isInstanceOf(WriteAccessPermissionException.class);

        verify(fileRepository, never()).saveFile(file, fileId);
    }

    @Test
    void ownerCanAlwaysDelete() throws MopsException {
        long dirId = 1;
        long fileId = 17;

        // no delete permission
        UserPermission userPermission = new UserPermission(true, false, false);
        doReturn(userPermission)
                .when(securityService)
                .getPermissionsOfUser(eq(account), any());

        FileInfo file = FileInfo.builder()
                .from(this.file)
                .id(fileId)
                .directory(dirId)
                .owner(account.getName()) // account is owner
                .build();

        doReturn(file)
                .when(fileInfoService)
                .fetchFileInfo(fileId);

        fileService.deleteFile(account, fileId);

        verify(fileInfoService, times(1)).deleteFileInfo(fileId);
        verify(fileRepository, times(1)).deleteFile(fileId);
    }

    @Test
    void noDeletePermission() throws MopsException {
        long dirId = 1;
        long fileId = 17;

        // no delete permission
        UserPermission userPermission = new UserPermission(true, false, false);
        doReturn(userPermission)
                .when(securityService)
                .getPermissionsOfUser(eq(account), any());

        FileInfo fileInfoStub = FileInfo.builder()
                .from(file)
                .id(fileId)
                .directory(dirId)
                .owner("notUser1234") // account is not the owner
                .build();

        doReturn(fileInfoStub)
                .when(fileInfoService)
                .fetchFileInfo(fileId);

        assertThatThrownBy(() -> {
            fileService.deleteFile(account, fileId);
        }).isInstanceOf(DeleteAccessPermissionException.class);

        verify(fileInfoService, never()).deleteFileInfo(fileId);
        verify(fileRepository, never()).deleteFile(fileId);
    }

    @Test
    void notOwnerButDeletePermission() throws MopsException {
        long dirId = 1;
        long fileId = 17;

        UserPermission userPermission = new UserPermission(true, false, true);
        doReturn(userPermission)
                .when(securityService)
                .getPermissionsOfUser(eq(account), any());

        FileInfo fileInfoStub = FileInfo.builder()
                .from(file)
                .id(fileId)
                .directory(dirId)
                .owner("notUser1234")
                .build();

        doReturn(fileInfoStub)
                .when(fileInfoService)
                .fetchFileInfo(fileId);

        fileService.deleteFile(account, fileId);

        verify(fileInfoService, times(1)).deleteFileInfo(fileId);
        verify(fileRepository, times(1)).deleteFile(fileId);
    }

    @Test
    void noPermissionToRetrieveFile() throws MopsException {
        long groupId = 2;
        long permId = 54;
        long dirId = 1;
        long fileId = 17;

        UserPermission userPermission = new UserPermission(false, false, true);
        doReturn(userPermission)
                .when(securityService)
                .getPermissionsOfUser(eq(account), any());

        Directory directory = Directory.builder()
                .id(dirId)
                .name("Test Dir")
                .groupOwner(groupId)
                .permissions(permId)
                .build();

        FileInfo fileInfoStub = FileInfo.builder()
                .from(file)
                .id(fileId)
                .directory(directory)
                .owner("notUser1234") // irrelevant here
                .build();

        doReturn(directory)
                .when(directoryService)
                .getDirectory(dirId);

        doReturn(fileInfoStub)
                .when(fileInfoService)
                .fetchFileInfo(fileId);

        doReturn(LocalDateTime.of(2019, 12, 31, 0, 0).toInstant(ZoneOffset.UTC))
                .when(timeService)
                .getInstantNow();

        assertThatThrownBy(() -> {
            fileService.getFile(account, fileId);
        }).isInstanceOf(ReadAccessPermissionException.class);

        verify(fileInfoService, atLeastOnce()).fetchFileInfo(fileId);
        verify(fileRepository, never()).getFileContent(fileId);
    }

    @Test
    void outOfAvailabilityRetrieveFile() throws MopsException {
        long groupId = 2;
        long permId = 54;
        long dirId = 1;
        long fileId = 17;

        UserPermission userPermission = new UserPermission(true, false, false);
        doReturn(userPermission)
                .when(securityService)
                .getPermissionsOfUser(eq(account), any());

        Directory directory = Directory.builder()
                .id(dirId)
                .name("Test Dir")
                .groupOwner(groupId)
                .permissions(permId)
                .build();

        FileInfo fileInfoStub = FileInfo.builder()
                .from(file)
                .id(fileId)
                .directory(directory)
                .owner("notUser1234") // irrelevant here
                .availableFrom(LocalDateTime.of(2020, 1, 1, 0, 0).toInstant(ZoneOffset.UTC))
                .build();

        doReturn(directory)
                .when(directoryService)
                .getDirectory(dirId);

        doReturn(fileInfoStub)
                .when(fileInfoService)
                .fetchFileInfo(fileId);

        doReturn(LocalDateTime.of(2019, 12, 31, 0, 0).toInstant(ZoneOffset.UTC))
                .when(timeService)
                .getInstantNow();

        assertThatThrownBy(() -> {
            fileService.getFile(account, fileId);
        }).isInstanceOf(ReadAccessPermissionException.class);

        verify(fileInfoService, atLeastOnce()).fetchFileInfo(fileId);
        verify(fileRepository, never()).getFileContent(fileId);
    }

    @Test
    void canRetrieveFile() throws MopsException, IOException {
        long groupId = 2;
        long permId = 54;
        long dirId = 1;
        long fileId = 17;

        UserPermission userPermission = new UserPermission(true, false, false);
        doReturn(userPermission)
                .when(securityService)
                .getPermissionsOfUser(eq(account), any());

        Directory directory = Directory.builder()
                .id(dirId)
                .name("Test Dir")
                .groupOwner(groupId)
                .permissions(permId)
                .build();

        FileInfo fileInfoStub = FileInfo.builder()
                .from(file)
                .id(fileId)
                .directory(directory)
                .owner("notUser1234")
                .build();

        doReturn(directory)
                .when(directoryService)
                .getDirectory(dirId);

        doReturn(fileInfoStub)
                .when(fileInfoService)
                .fetchFileInfo(fileId);

        doReturn(file.getInputStream())
                .when(fileRepository)
                .getFileContent(fileId);

        doReturn(LocalDateTime.of(2019, 12, 31, 0, 0).toInstant(ZoneOffset.UTC))
                .when(timeService)
                .getInstantNow();

        FileContainer fileContainer = fileService.getFile(account, fileId);

        verify(fileInfoService, atLeastOnce()).fetchFileInfo(fileId);
        verify(fileRepository, times(1)).getFileContent(fileId);

        byte[] originalContent = file.getBytes();
        byte[] retrievedData = fileContainer.getContent().getInputStream().readAllBytes();

        assertThat(originalContent).isEqualTo(retrievedData);
    }

    @Test
    void renameAFile() throws MopsException {
        long dirId = 1;
        long fileId = 1;

        UserPermission userPermission = new UserPermission(false, true, true);
        doReturn(userPermission)
                .when(securityService)
                .getPermissionsOfUser(eq(account), any());

        FileInfo fileInfoStub = FileInfo.builder()
                .from(file)
                .id(fileId)
                .directory(dirId)
                .owner("notUser1234")
                .build();

        FileInfo fileInfoSpy = spy(fileInfoStub);

        doReturn(fileInfoSpy)
                .when(fileInfoService)
                .fetchFileInfo(fileId);

        fileService.renameFile(account, fileId, "new /Name-file");

        verify(fileInfoService, times(1)).saveFileInfo(any());
        // verify new name with old extension
        verify(fileInfoSpy).setName("new__Name-file.bin");
    }

    @Test
    void renamePermissions() throws MopsException {
        long dirId = 1;
        long fileId = 17;

        FileInfo fileInfoStub = FileInfo.builder()
                .from(file)
                .id(fileId)
                .directory(dirId)
                .owner("notUser1234") // irrelevant here
                .build();

        doReturn(fileInfoStub)
                .when(fileInfoService)
                .fetchFileInfo(fileId);

        UserPermission noPerms = new UserPermission(false, false, false);
        UserPermission deletePerm = new UserPermission(false, false, true);
        UserPermission writePerm = new UserPermission(false, true, false);
        UserPermission writeAndDeletePerm = new UserPermission(false, true, true);

        when(securityService.getPermissionsOfUser(any(), any()))
                .thenReturn(noPerms, deletePerm, writePerm, writeAndDeletePerm);

        assertThatThrownBy(() -> {
            fileService.renameFile(account, fileId, "newName");
        }).isInstanceOf(WriteAccessPermissionException.class);

        assertThatThrownBy(() -> {
            fileService.renameFile(account, fileId, "newName");
        }).isInstanceOf(WriteAccessPermissionException.class);

        assertThatThrownBy(() -> {
            fileService.renameFile(account, fileId, "newName");
        }).isInstanceOf(WriteAccessPermissionException.class);

        assertThatCode(() -> {
            fileService.renameFile(account, fileId, "newName");
        }).doesNotThrowAnyException();
    }

    @Test
    void noNameException() {
        long fileId = 17;

        assertThatThrownBy(() -> {
            fileService.renameFile(account, fileId, "");
        }).isInstanceOf(EmptyNameException.class);
    }

    private byte[] getRandomBytes() {
        int fileLength = random.nextInt(10000) + 1;
        byte[] bytes = new byte[fileLength];
        random.nextBytes(bytes);
        return bytes;
    }
}
