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

    @Test
    void stringsWithSpaces() {
        FileQuery expected = FileQuery.builder()
                .owner("Jens Bendisposto")
                .build();

        FileQuery actual = INSTANCE.convert("owner:\"jens bendisposto\"");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testUnnecessaryQuotationmarks() {
        FileQuery expected = FileQuery.builder()
                .name("name")
                .owner("Jens Bendisposto")
                .build();

        FileQuery actual = INSTANCE.convert("\"owner\":\"jens bendisposto\" \"name\"");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testEscaping() {
        FileQuery expected = FileQuery.builder()
                .owner("Jens\"Bendisposto")
                .build();

        FileQuery actual = INSTANCE.convert("\"owner\":\"jens\\\"bendisposto\"");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testNoEscapingAndStringsInRawLiterals() {
        FileQuery expected = FileQuery.builder()
                .owner("Jens\\B\"endis\"posto")
                .build();

        FileQuery actual = INSTANCE.convert("owner:jens\\b\"endis\"posto");

        assertThat(actual).isEqualTo(expected);
    }
}
