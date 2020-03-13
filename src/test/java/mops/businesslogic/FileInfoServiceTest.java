package mops.businesslogic;

import mops.exception.MopsException;
import mops.persistence.FileInfoRepository;
import mops.persistence.FileRepository;
import mops.persistence.file.FileInfo;
import mops.utils.TestContext;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.assertj.core.api.Assertions.assertThat;

@TestContext
@SpringBootTest
public class FileInfoServiceTest {

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
    void setUp() {
        fileInfoList = new ArrayList<FileInfo>();
        fileInfo1 = mock(FileInfo.class);
        fileInfoList.add(fileInfo1);

        given(fileInfoRepository
                .getAllFileInfoByDirectory(1L))
                .willReturn(fileInfoList);
    }

    @Test
    public void fetchAllFilesInDirectory() throws MopsException {
        List<FileInfo> result = fileInfoService.fetchAllFilesInDirectory(1L);
        assertThat(result).isEqualTo(fileInfoList);
    }

    void fetchFileInfo() {

    }

    void saveFileInfo() {

    }

    void deleteFileInfo() {

    }

}
