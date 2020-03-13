package mops.businesslogic;

import mops.persistence.file.FileInfo;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FileQueryImplTest {
    @Test
    public void findOwnerTest() {
        FileQuery fileQuery = FileQuery.builder()
                .owners(List.of("iTitus"))
                .build();
        FileInfo fileInfo = FileInfo.builder()
                .id(1L)
                .name("cv")
                .directory(1L)
                .type("pdf")
                .size(80L)
                .owner("iTitus")
                .build();

        assertThat(fileQuery.checkMatch(fileInfo)).isTrue();
    }
}