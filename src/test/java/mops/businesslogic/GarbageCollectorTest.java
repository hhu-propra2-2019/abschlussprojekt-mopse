package mops.businesslogic;

import mops.businesslogic.directory.DeleteService;
import mops.businesslogic.directory.DirectoryService;
import mops.businesslogic.file.FileInfoService;
import mops.businesslogic.file.FileServiceImpl;
import mops.businesslogic.group.GroupService;
import mops.exception.MopsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

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
    void shouldDeleteOrphans() throws MopsException {
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
}
