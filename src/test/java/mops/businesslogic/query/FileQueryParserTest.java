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
                .name("foo")
                .build();

        FileQuery actual = INSTANCE.convert("foo");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void full() {
        FileQuery expected = FileQuery.builder()
                .name("foo")
                .name("bar")
                .owner("jens")
                .owner("chris")
                .type("application/pdf")
                .tag("skript")
                .build();

        FileQuery actual = INSTANCE.convert("owner:jens owner:chris tag:skript type:application/pdf name:bar foo");

        assertThat(actual).isEqualTo(expected);
    }
}
