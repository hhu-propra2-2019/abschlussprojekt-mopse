package mops.businesslogic;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class FileQueryForm {
    private String[] fileNames;

    private String[] owners;

    private String[] types;

    private String[] tags;
}
