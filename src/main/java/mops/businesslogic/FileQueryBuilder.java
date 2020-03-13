package mops.businesslogic;

import java.util.List;

public class FileQueryBuilder {
    private List<String> owners;

    /**
     * @param owners list of owner to search for
     * @return this
     */
    public FileQueryBuilder owners(List<String> owners) {
        this.owners = owners;
        return this;
    }

    /**
     * Builds the object from it's information.
     *
     * @return file query object
     */
    public FileQuery build() {
        return new FileQueryImpl(
                owners
        );
    }
}
