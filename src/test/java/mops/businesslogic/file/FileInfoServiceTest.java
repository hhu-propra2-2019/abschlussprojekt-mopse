package mops.businesslogic.file;

import mops.businesslogic.exception.DatabaseDuplicationException;
import mops.exception.MopsException;
import mops.persistence.FileInfoRepository;
import mops.persistence.file.FileInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class FileInfoServiceTest {

    @Mock
    FileInfoRepository fileInfoRepository;

    FileInfoService fileInfoService;

    FileInfo file1;

    @BeforeEach
    void setup() {
        fileInfoService = new FileInfoServiceImpl(fileInfoRepository);

        file1 = FileInfo.builder()
                .name("file")
                .directory(1L)
                .type("type")
                .size(0)
                .owner("user")
                .build();
    }

    @Test
    void fetchAllFilesInDirectory() throws MopsException {
        given(fileInfoRepository.findAllInDirectory(1L)).willReturn(List.of(file1));

        List<FileInfo> result = fileInfoService.fetchAllFilesInDirectory(1L);

        assertThat(result).containsExactlyInAnyOrder(file1);
    }

    @Test
    void fetchAllFilesInDirectoryError() {
        given(fileInfoRepository.findAllInDirectory(2L)).willThrow(DbActionExecutionException.class);

        assertThatThrownBy(() -> fileInfoService.fetchAllFilesInDirectory(2L))
                .isInstanceOf(MopsException.class);
    }

    @Test
    void fetchFileInfo() throws MopsException {
        given(fileInfoRepository.findById(1L)).willReturn(Optional.of(file1));

        FileInfo result = fileInfoService.fetchFileInfo(1L);

        assertThat(result).isEqualTo(file1);
    }

    @Test
    void fetchFileInfoError() {
        given(fileInfoRepository.findById(2L)).willThrow(DbActionExecutionException.class);

        assertThatThrownBy(() -> fileInfoService.fetchFileInfo(2L))
                .isInstanceOf(MopsException.class);
    }

    @Test
    void saveFileInfo() throws MopsException {
        given(fileInfoRepository.save(file1)).willReturn(file1);

        FileInfo result = fileInfoService.saveFileInfo(file1);

        assertThat(result).isEqualTo(file1);
    }

    @Test
    void saveFileInfoError() {
        given(fileInfoRepository.save(file1)).willThrow(DbActionExecutionException.class);

        assertThatThrownBy(() -> fileInfoService.saveFileInfo(file1))
                .isInstanceOf(MopsException.class);
    }

    @Test
    void deleteFileInfo() {
        willDoNothing().given(fileInfoRepository).deleteById(1L);

        assertThatCode(() -> fileInfoService.deleteFileInfo(1L))
                .doesNotThrowAnyException();
    }

    @Test
    void deleteFileInfoError() {
        willThrow(DbActionExecutionException.class).given(fileInfoRepository).deleteById(2L);

        assertThatThrownBy(() -> fileInfoService.deleteFileInfo(2L))
                .isInstanceOf(MopsException.class);
    }

    @Test
    public void duplicatonTest() throws MopsException {
        fileInfoService.saveFileInfo(file1);
        DbActionExecutionException exception = mock(DbActionExecutionException.class);
        given(exception.getCause()).willReturn(mock(DuplicateKeyException.class));
        willThrow(exception).given(fileInfoRepository).save(file1);
        assertThatExceptionOfType(DatabaseDuplicationException.class).isThrownBy(() -> fileInfoService.saveFileInfo(file1));
    }
}
