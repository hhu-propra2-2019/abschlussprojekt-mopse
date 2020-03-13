package mops.businesslogic;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import mops.persistence.file.FileInfo;
import mops.persistence.file.FileTag;

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
     * List of file tags to search for.
     */
    private List<FileTag> tags;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkMatch(FileInfo file) {
        return checkNames(file) &&
                checkOwners(file) &&
                checkTypes(file) &&
                checkTags(file);
    }

    private boolean checkTags(FileInfo file) {
        boolean anyMatch = tags.stream()
                .anyMatch(tag -> file.getTags().contains(tag));
        return tags.isEmpty() || anyMatch;
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
