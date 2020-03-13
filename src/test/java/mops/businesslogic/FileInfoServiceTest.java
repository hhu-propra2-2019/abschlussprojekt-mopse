package mops.businesslogic;

import mops.exception.MopsException;
import mops.persistence.FileInfoRepository;
import mops.persistence.FileRepository;
import mops.persistence.file.FileInfo;
import mops.utils.TestContext;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

@TestContext
@SpringBootTest
class FileInfoServiceTest {

    @Rule
    public final ExpectedException mopsEx = ExpectedException.none();

    @MockBean
    GroupService groupService;
    @MockBean
    FileRepository fileRepository;
    @MockBean
    FileService fileService;
    @MockBean
    PermissionService permissionService;
    @MockBean
    FileInfoRepository fileInfoRepository;

    @Autowired
    FileInfoService fileInfoService;

    List<FileInfo> fileInfoList;
    FileInfo fileInfo1;

    @BeforeEach
    void setup() {
        fileInfoList = new ArrayList<FileInfo>();
        fileInfo1 = mock(FileInfo.class);
        fileInfoList.add(fileInfo1);

        given(fileInfoRepository.getAllFileInfoByDirectory(1L)).willReturn(fileInfoList);
        given(fileInfoRepository.save(fileInfo1)).willReturn(fileInfo1);

        willDoNothing().given(fileInfoRepository).deleteById(1L);
        willThrow(DbActionExecutionException.class).given(fileInfoRepository).deleteById(2L);
    }

    @Test
    public void fetchAllFilesInDirectory() throws MopsException {
        List<FileInfo> result = fileInfoService.fetchAllFilesInDirectory(1L);
        assertThat(result).isEqualTo(fileInfoList);
    }

    void fetchFileInfo() {

    }

    @Test
    void saveFileInfo() throws MopsException {
        assertThat(fileInfoService.saveFileInfo(fileInfo1)).isEqualTo(fileInfo1);
    }

    @Test
    void deleteFileInfo() {
        assertThatCode(() -> fileInfoService.deleteFileInfo(1L))
                .doesNotThrowAnyException();
    }

    @Test
    void deleteFileInfoThatDoesntExist() {
        assertThatExceptionOfType(MopsException.class)
                .isThrownBy(() -> fileInfoService.deleteFileInfo(2L));
    }
}
