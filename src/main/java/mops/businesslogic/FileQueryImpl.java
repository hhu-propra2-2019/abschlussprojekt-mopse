package mops.businesslogic;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import mops.persistence.file.FileInfo;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class FileQueryImpl implements FileQuery {
    /**
     * List of user names of file owners to search for.
     */
    private List<String> owners;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkMatch(FileInfo file) {
        if (!checkOwners(file)) {
            return false;
        }
        return true;
    }

    private boolean checkOwners(FileInfo file) {
        return owners.contains(file.getOwner());
    }
}
