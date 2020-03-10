package mops.businesslogic;

import lombok.AllArgsConstructor;
import mops.persistence.DirectoryRepository;
import mops.persistence.directory.Directory;
import mops.persistence.file.FileInfo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@AllArgsConstructor
public class DirectoryServiceImpl implements DirectoryService {

    /**
     * This connects to database related to directory information.
     */
    private final DirectoryRepository directoryRepository;

    /**
     * Uploads a file.
     *
     * @param account  user credentials
     * @param dirId    the id of the folder where the file will be uploaded
     * @param multipartFile the file object
     */
    @Override
    public FileInfo uploadFile(Account account, long dirId, MultipartFile multipartFile) {return null; }

    /**
     * Returns all folders of the parent folder.
     *
     * @param account user credentials
     * @param dirId   id of the folder
     * @return list of folders
     */
    @Override
    public List<Directory> getSubFolders(Account account, long dirId) {


        return null;
    }

    /**
     * Creates a new folder inside a folder.
     *
     * @param account     user credentials
     * @param parentDirId id of the parent folder
     * @param dirName     name of the new folder
     * @return id of the new folder
     */
    @Override
    public long createFolder(Account account, long parentDirId, String dirName) {
        return 0;
    }

    /**
     * Deletes a folder.
     *
     * @param account user credential
     * @param dirId   id of the folder to be deleted
     * @return the parent id of the deleted folder
     */
    @Override
    public long deleteFolder(Account account, long dirId) {
        return 0;
    }

    /**
     * Searches a folder for files.
     *
     * @param account user credentials
     * @param dirId   id of the folder to be searched
     * @param query   wrapper object of the query parameter
     * @return list of files
     */
    @Override
    public List<FileInfo> searchFolder(Account account, long dirId, FileQuery query) {
        return null;
    }
}
