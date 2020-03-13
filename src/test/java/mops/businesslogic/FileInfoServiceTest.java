package mops.businesslogic;

import mops.exception.MopsException;
import mops.persistence.FileInfoRepository;
import mops.persistence.FileRepository;
import mops.persistence.file.FileInfo;
import mops.utils.TestContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@TestContext
@SpringBootTest
class FileInfoServiceTest {

    @MockBean
    FileInfoRepository fileInfoRepository;
    @MockBean
    GroupService groupService;
    @MockBean
    FileRepository fileRepository;
    @MockBean
    FileService fileService;
    @MockBean
    PermissionService permissionService;

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
        given(fileInfoRepository.getFileInfoById(3L)).willReturn(fileInfo1);
    }

    @Test
    public void fetchAllFilesInDirectory() throws MopsException {
        List<FileInfo> result = fileInfoService.fetchAllFilesInDirectory(1L);
        assertThat(result).isEqualTo(fileInfoList);
    }

    @Test
    void fetchFileInfo() throws MopsException {
        FileInfo  result = fileInfoService.fetchFileInfo(3L);
        assertThat(result).isEqualTo(fileInfo1);
    }

    void saveFileInfo() {

    }

    void deleteFileInfo() {

    }
}
