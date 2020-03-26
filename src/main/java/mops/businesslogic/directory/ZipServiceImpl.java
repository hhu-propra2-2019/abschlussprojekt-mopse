package mops.businesslogic.directory;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import mops.businesslogic.exception.MopsZipsException;
import mops.businesslogic.file.FileContainer;
import mops.businesslogic.file.FileService;
import mops.businesslogic.security.Account;
import mops.exception.MopsException;
import mops.persistence.directory.Directory;
import mops.persistence.file.FileInfo;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Zips content from File Storage.
 */
@Slf4j
@Service
@AllArgsConstructor
public class ZipServiceImpl implements ZipService {
    /**
     * Handles requests concerning directories.
     */
    private final DirectoryService directoryService;
    /**
     * Handles file requests.
     */
    private final FileService fileService;
    /**
    * {@inheritDoc}
     */
    @Override
    public ZipOutputStream zipDirectory(Account account, long dirId) throws MopsException {
        Directory directory = directoryService.getDirectory(dirId);
        FileOutputStream fileOutputStream;
        @NonNull String directoryName = directory.getName();
        try {
            fileOutputStream = new FileOutputStream(String.format("%s.zip", directoryName));
        } catch (FileNotFoundException e) {
            log.error("Failed to create FileOutputStream for {}", directoryName);
            throw new MopsException("Interner Fehler beim zippen.");
        }
        ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);

        List<FileInfo> files = fileService.getFilesOfDirectory(account, dirId);

        zipDirectory(account, directoryName, zipOutputStream, files);

        return zipOutputStream;
    }

    private void zipDirectory(Account account,
                              @NonNull String directoryName,
                              ZipOutputStream zipOutputStream,
                              List<FileInfo> files) throws MopsException {
        try {
            zipOutputStream.putNextEntry(new ZipEntry(String.format("%s/", directoryName)));
        } catch (IOException e) {
            log.error("Failed create zip entry for directory '{}", directoryName);
            throw new MopsZipsException(String.format("Der Ornder '%s' konnte nicht gezippt werden.", directoryName));
        }

        for (FileInfo fileInfo : files) {
            zipFile(account, zipOutputStream, fileInfo, directoryName);

        }
        try {
            zipOutputStream.closeEntry();
        } catch (IOException e) {
            log.error("Failed to close zip entry for '{}", directoryName);
            throw new MopsZipsException(String.format("Der zip konnte für '%s' nicht beendet werden", directoryName));
        }
    }

    private void zipFile(Account account,
                         ZipOutputStream zipOutputStream,
                         FileInfo fileInfo,
                         String path) throws MopsException {
        FileContainer fileContainer = fileService.getFile(account, fileInfo.getId());
        @NonNull String fileName = fileInfo.getName();
        ZipEntry zipEntry = new ZipEntry(String.format("%s/%s", path, fileName));
        try {
            zipOutputStream.putNextEntry(zipEntry);
        } catch (IOException e) {
            log.error("Failed to zip file '{}.", fileName);
            throw new MopsZipsException(String.format("Die Datei '%s' konnte nicht gezippt werden.",
                    fileName));
        }
        try {
            zipOutputStream.write(fileContainer.getContent().getInputStream().readAllBytes());
        } catch (IOException e) {
            log.error("Failed to get file content from '{}'", fileName);
            String message = String.format("Von '%s' konnte der Dateiinhalt nicht gelesen werden.", fileName);
            throw new mops.businesslogic.exception.FileNotFoundException(message);
        }
        try {
            zipOutputStream.closeEntry();
        } catch (IOException e) {
            log.error("Failed to close zip entry for '{}", fileName);
            throw new MopsZipsException(String.format("Der zip konnte für '%s' nicht beendet werden", fileName));
        }
    }
}
