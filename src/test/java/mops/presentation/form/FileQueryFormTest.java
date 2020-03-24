package mops.presentation.form;

import mops.businesslogic.file.query.FileQuery;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FileQueryFormTest {

    @Test
    void emptyStringTest() {
        FileQueryForm fileQueryForm = new FileQueryForm();
        fileQueryForm.setNames(new String[] { "" });
        fileQueryForm.setOwners(new String[] { "ich" });

        FileQuery expected = FileQuery.builder()
                .owner("ich")
                .build();

        FileQuery fileQuery = fileQueryForm.toQuery();

        assertThat(fileQuery).isEqualTo(expected);
    }
}
