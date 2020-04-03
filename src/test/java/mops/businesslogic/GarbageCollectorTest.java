package mops.businesslogic;

import mops.businesslogic.directory.DeleteService;
import mops.businesslogic.directory.DirectoryService;
import mops.businesslogic.file.FileInfoService;
import mops.businesslogic.file.FileServiceImpl;
import mops.businesslogic.group.GroupService;
import mops.exception.MopsException;
import mops.persistence.directory.Directory;
import mops.persistence.group.Group;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GarbageCollectorTest {

    @Mock
    FileServiceImpl fileService;
    @Mock
    FileInfoService fileInfoService;
    @Mock
    DeleteService deleteService;
    @Mock
    GroupService groupService;
    @Mock
    DirectoryService directoryService;

    GarbageCollector garbageCollector;


    @BeforeEach
    void prepare() {
        garbageCollector = new GarbageCollector(fileInfoService,
                fileService,
                deleteService,
                groupService,
                directoryService);
    }

    @Test
    void shouldDeleteOrphanedFiles() throws MopsException {
        Set<Long> givenMetaIds = Set.of(1L, 2L, 3L, 4L, 5L, 7L); // 2 is orphaned
        Set<Long> givenFileIds = Set.of(1L, 3L, 4L, 5L, 6L, 7L); // 6 is orphaned

        doReturn(givenFileIds)
                .when(fileService)
                .getAllFileIds();

        doReturn(givenMetaIds)
                .when(fileInfoService)
                .fetchAllFileInfoIds();

        garbageCollector.removeOrphanedFiles();

        verify(fileService, times(1)).deleteFileWithoutMeta(6);
        verify(fileInfoService, times(1)).deleteFileInfo(2);

        for (Long fileId : givenFileIds) {
            verify(fileInfoService, never()).deleteFileInfo(fileId);
        }

        for (Long metaId : givenMetaIds) {
            verify(fileService, never()).deleteFileWithoutMeta(metaId);
        }
    }

    @Test
    void shouldRemovedOrphanedDirectories() throws MopsException {
        Directory dir1 = Directory.builder()
                .id(1L)
                .name("Dir_from_group_1")
                .groupOwner(1)
                .permissions(1)
                .build();
        Directory dir2 = Directory.builder()
                .id(2L)
                .name("Dir_from_non_existing_group")
                .groupOwner(2)
                .permissions(2)
                .build();
        Directory dir3 = Directory.builder()
                .id(3L)
                .name("Dir_from_group_3")
                .groupOwner(3)
                .permissions(3)
                .build();

        Group group1 = Group.builder()
                .id(1L)
                .groupId(UUID.randomUUID())
                .name("Group 1")
                .build();

        Group group3 = Group.builder()
                .id(3L)
                .groupId(UUID.randomUUID())
                .name("Group 3")
                .build();

        List<Directory> dirStubs = List.of(dir1, dir2, dir3);
        List<Group> groupStubs = List.of(group1, group3);

        doReturn(dirStubs)
                .when(directoryService)
                .getAllRootDirectories();

        doReturn(groupStubs)
                .when(groupService)
                .getAllGroups();

        garbageCollector.removeOrphanedDirs();

        verify(deleteService, times(1)).deleteFolder(any(), eq(2L));
        verify(deleteService, never()).deleteFolder(any(), eq(1L));
        verify(deleteService, never()).deleteFolder(any(), eq(3L));
    }
}
