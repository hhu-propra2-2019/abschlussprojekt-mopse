package mops.businesslogic;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class FileQueryBuilder {
    /**
     * List of owner to search for.
     */
    private List<String> owners;

    /**
     * Names of files to search for.
     */
    private List<String> fileNames;
    /**
     * File types to search for.
     */
    private List<String> types;

    /**
     * Builds the object from it's information.
     *
     * @return file query object
     */
    public FileQuery build() {
        if (fileNames == null) {
            fileNames = List.of();
        }
        if (owners == null) {
            owners = List.of();
        }
        if (types == null) {
            types = List.of();
        }
        return new FileQueryImpl(
                fileNames,
                owners
        );
    }

    /**
     * @param owners list of owner to search for
     * @return this
     */
    public FileQueryBuilder owners(List<String> owners) {
        this.owners = owners;
        return this;
    }


    /**
     * @param fileNames names of files to search for
     * @return this
     */
    public FileQueryBuilder names(List<String> fileNames) {
        this.fileNames = fileNames;
        return this;
    }

    public FileQueryBuilder types(List<String> types) {
        this.types = types;
        return this;
    }
}
