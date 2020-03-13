package mops.businesslogic;

import mops.persistence.file.FileInfo;
import mops.persistence.file.FileTag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FileQueryImplTest {

    private FileInfo fileInfo;

    @BeforeEach
    void setUp() {
        fileInfo = FileInfo.builder()
                .id(1L)
                .name("cv")
                .directory(1L)
                .type("pdf")
                .size(80L)
                .owner("iTitus")
                .tag("Hausaufgaben")
                .build();
    }

    @Test
    public void findOwnerTest() {
        FileQuery fileQuery = FileQuery.builder()
                .owners(List.of("iTitus"))
                .build();

        assertThat(fileQuery.checkMatch(fileInfo)).isTrue();
    }

    @Test
    public void noMatchingOwnerTest() {
        FileQuery fileQuery = FileQuery.builder()
                .owners(List.of("Segelzwerg"))
                .build();

        assertThat(fileQuery.checkMatch(fileInfo)).isFalse();
    }

    @Test
    public void findFileNameTest() {
        FileQuery fileQuery = FileQuery.builder()
                .names(List.of("cv"))
                .build();

        assertThat(fileQuery.checkMatch(fileInfo)).isTrue();
    }

    @Test
    public void noMatchingFileNameTest() {
        FileQuery fileQuery = FileQuery.builder()
                .names(List.of("Lebenslauf"))
                .build();

        assertThat(fileQuery.checkMatch(fileInfo)).isFalse();
    }

    @Test
    public void findTypeTest() {
        FileQuery fileQuery = FileQuery.builder()
                .types(List.of("pdf"))
                .build();
        assertThat(fileQuery.checkMatch(fileInfo)).isTrue();
    }

    @Test
    public void noMatchingTypeTest() {
        FileQuery fileQuery = FileQuery.builder()
                .types(List.of("png"))
                .build();
        assertThat(fileQuery.checkMatch(fileInfo)).isFalse();
    }

    @Test
    public void findTagTest() {
        FileQuery fileQuery = FileQuery.builder()
                .tags(List.of(new FileTag("Hausaufgaben")))
                .build();
        assertThat(fileQuery.checkMatch(fileInfo)).isTrue();
    }

    @Test
    public void noMatchingTagTest() {
        FileQuery fileQuery = FileQuery.builder()
                .tags(List.of(new FileTag("lösungen")))
                .build();
        assertThat(fileQuery.checkMatch(fileInfo)).isFalse();
    }
}