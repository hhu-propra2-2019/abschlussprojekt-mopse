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
        return checkNames(file) && checkOwners(file) && checkTypes(file);
    }

    private boolean checkTypes(FileInfo file) {
        return types.isEmpty() || types.contains(file.getType());
    }

    private boolean checkNames(FileInfo file) {
        return fileNames.isEmpty() || fileNames.contains(file.getName());
    }

    private boolean checkOwners(FileInfo file) {
        return owners.isEmpty() || owners.contains(file.getOwner());
    }
}
