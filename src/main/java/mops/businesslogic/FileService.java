package mops.businesslogic;

import mops.persistence.file.FileInfo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Service
public interface FileService {
    /**
     * @param account user credentials
     * @param dirId   directory id
     * @return a list of files
     */
    //TODO: change return to list of FileContainer
    List<FileInfo> getFilesOfDirectory(Account account, long dirId);

    void uploadFile(Account account, long dirId, MultipartFile multipartFile, Set<Object> of);
}
