package mops.businesslogic.query;

import org.junit.jupiter.api.Test;

import static mops.businesslogic.query.FileQueryConverter.INSTANCE;
import static org.assertj.core.api.Assertions.assertThat;

class FileQueryParserTest {

    @Test
    void empty() {
        FileQuery expected = FileQuery.builder()
                .build();

        FileQuery actual = INSTANCE.convert("");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void valueWithoutKey() {
        FileQuery expected = FileQuery.builder()
                .fileName("foo")
                .build();

        FileQuery actual = INSTANCE.convert("foo");

        assertThat(actual).isEqualTo(expected);
    }
}
