package mops.businesslogic;

import mops.persistence.file.FileInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FileQueryTest {

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
                .tags(List.of("Hausaufgaben"))
                .build();
        assertThat(fileQuery.checkMatch(fileInfo)).isTrue();
    }

    @Test
    public void noMatchingTagTest() {
        FileQuery fileQuery = FileQuery.builder()
                .tags(List.of("lösungen"))
                .build();
        assertThat(fileQuery.checkMatch(fileInfo)).isFalse();
    }

    @Test
    public void oneMatchesOneDoesNotTest() {
        FileQuery fileQuery = FileQuery.builder()
                .types(List.of("pdf"))
                .tags(List.of("lösungen"))
                .build();
        assertThat(fileQuery.checkMatch(fileInfo)).isFalse();
    }

    @Test
    public void twoMatches() {
        FileQuery fileQuery = FileQuery.builder()
                .owners(List.of("iTitus"))
                .names(List.of("cv"))
                .build();
        assertThat(fileQuery.checkMatch(fileInfo)).isTrue();

    }
}
