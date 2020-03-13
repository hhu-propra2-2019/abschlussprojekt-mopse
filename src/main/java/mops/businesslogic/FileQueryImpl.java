package mops.businesslogic;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import mops.persistence.file.FileInfo;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class FileQueryImpl implements FileQuery {
    /**
     * List of file names to search for.
     */
    private List<String> fileNames;
    /**
     * List of user names of file owners to search for.
     */
    private List<String> owners;
    /**
     * List of file types to search for.
     */
    private List<String> types;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkMatch(FileInfo file) {
        if (!checkOwners(file)) {
            return false;
        }
        if (!checkNames(file)) {
            return false;
        }
        if (!checkTypes(file)) {
            return false;
        }
        return true;
    }

    private boolean checkTypes(FileInfo file) {
        if (types.isEmpty()) {
            return true;
        }
        return types.contains(file.getType());
    }

    private boolean checkNames(FileInfo file) {
        if (fileNames.isEmpty()) {
            return true;
        }
        return fileNames.contains(file.getName());
    }

    private boolean checkOwners(FileInfo file) {
        if (owners.isEmpty()) {
            return true;
        }
        return owners.contains(file.getOwner());
    }
}
