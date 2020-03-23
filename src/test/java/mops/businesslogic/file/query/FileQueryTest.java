package mops.businesslogic.file.query;

import mops.persistence.file.FileInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FileQueryTest {

    FileInfo fileInfo;

    @BeforeEach
    void setup() {
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
    void findOwnerTest() {
        FileQuery fileQuery = FileQuery.builder()
                .owner("iTitus")
                .build();

        assertThat(fileQuery.checkMatch(fileInfo)).isTrue();
    }

    @Test
    void noMatchingOwnerTest() {
        FileQuery fileQuery = FileQuery.builder()
                .owner("Segelzwerg")
                .build();

        assertThat(fileQuery.checkMatch(fileInfo)).isFalse();
    }

    @Test
    void findFileNameTest() {
        FileQuery fileQuery = FileQuery.builder()
                .name("cv")
                .build();

        assertThat(fileQuery.checkMatch(fileInfo)).isTrue();
    }

    @Test
    void noMatchingFileNameTest() {
        FileQuery fileQuery = FileQuery.builder()
                .name("Lebenslauf")
                .build();

        assertThat(fileQuery.checkMatch(fileInfo)).isFalse();
    }

    @Test
    void findTypeTest() {
        FileQuery fileQuery = FileQuery.builder()
                .type("pdf")
                .build();
        assertThat(fileQuery.checkMatch(fileInfo)).isTrue();
    }

    @Test
    void noMatchingTypeTest() {
        FileQuery fileQuery = FileQuery.builder()
                .type("png")
                .build();
        assertThat(fileQuery.checkMatch(fileInfo)).isFalse();
    }

    @Test
    void findTagTest() {
        FileQuery fileQuery = FileQuery.builder()
                .tag("Hausaufgaben")
                .build();
        assertThat(fileQuery.checkMatch(fileInfo)).isTrue();
    }

    @Test
    void noMatchingTagTest() {
        FileQuery fileQuery = FileQuery.builder()
                .tag("lösungen")
                .build();
        assertThat(fileQuery.checkMatch(fileInfo)).isFalse();
    }

    @Test
    void oneMatchesOneDoesNotTest() {
        FileQuery fileQuery = FileQuery.builder()
                .type("pdf")
                .tag("lösungen")
                .build();
        assertThat(fileQuery.checkMatch(fileInfo)).isFalse();
    }

    @Test
    void twoMatches() {
        FileQuery fileQuery = FileQuery.builder()
                .owner("iTitus")
                .name("cv")
                .build();
        assertThat(fileQuery.checkMatch(fileInfo)).isTrue();
    }
}
