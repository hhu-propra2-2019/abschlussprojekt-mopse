package mops.businesslogic;

import mops.persistence.file.FileInfo;
import org.springframework.core.io.Resource;

public interface FileContainer {

    /**
     * @return file info of file
     */
    FileInfo getFileInfo();
    /**
     * @return parent directory of file
     */
    long getDirectoryId();

    /**
     *
     * @return content of file
     */
    Resource[] getContent();
}
