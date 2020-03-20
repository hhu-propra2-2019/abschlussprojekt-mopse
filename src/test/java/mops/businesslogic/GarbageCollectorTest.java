package mops.businesslogic;

import mops.exception.MopsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Set;

import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GarbageCollectorTest {

    GarbageCollector garbageCollector;
    FileServiceImpl fileService;
    FileInfoService fileInfoService;

    @BeforeEach
    void prepare() {
        fileService = mock(FileServiceImpl.class);
        fileInfoService = mock(FileInfoService.class);
        garbageCollector = new GarbageCollector(fileInfoService, fileService);
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

        garbageCollector.removeOrphans();

        verify(fileService, times(1)).deleteFile(6);
        verify(fileInfoService, times(1)).deleteFileInfo(2);

        for (Long fileId : givenFileIds) {
            verify(fileInfoService, never()).deleteFileInfo(fileId);
        }

        for (Long metaId : givenMetaIds) {
            verify(fileService, never()).deleteFile(metaId);
        }
    }
}
