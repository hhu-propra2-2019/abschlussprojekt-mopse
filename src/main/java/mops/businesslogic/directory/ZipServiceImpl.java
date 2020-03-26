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

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        OutputStream fileOutputStream;
        @NonNull String directoryName = directory.getName();
        try {
            Path path = Paths.get(String.format("%s.zip", directoryName));
            fileOutputStream = Files.newOutputStream(path);
        } catch (IOException e) {
            log.error("Failed to create FileOutputStream for {}", directoryName);
            throw new MopsException("Interner Fehler beim zippen.", e);
        }
        ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);


        zipDirectory(account, directoryName, zipOutputStream, dirId);

        return zipOutputStream;
    }

    private void zipDirectory(Account account,
                              @NonNull String directoryName,
                              ZipOutputStream zipOutputStream,
                              long dirId) throws MopsException {

        try {
            zipOutputStream.putNextEntry(new ZipEntry(String.format("%s/", directoryName)));
        } catch (IOException e) {
            log.error("Failed create zip entry for directory '{}", directoryName);
            throw new MopsZipsException(String.format("Der Ornder '%s' konnte nicht gezippt werden.", directoryName), e);
        }

        List<Directory> directories = directoryService.getSubFolders(account, dirId);
        for (Directory directory : directories) {
            String path = String.format("%s/%s", directoryName, directory.getName());
            zipDirectory(account, path, zipOutputStream, directory.getId());
        }

        List<FileInfo> files = fileService.getFilesOfDirectory(account, dirId);

        for (FileInfo fileInfo : files) {
            zipFile(account, zipOutputStream, fileInfo, directoryName);
        }

        try {
            zipOutputStream.closeEntry();
        } catch (IOException e) {
            log.error("Failed to close zip entry for '{}", directoryName);
            throw new MopsZipsException(String.format("Der zip konnte für '%s' nicht beendet werden", directoryName), e);
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
                    fileName), e);
        }
        try {
            zipOutputStream.write(fileContainer.getContent().getInputStream().readAllBytes());
        } catch (IOException e) {
            log.error("Failed to get file content from '{}'", fileName);
            String message = String.format("Von '%s' konnte der Dateiinhalt nicht gelesen werden.", fileName);
            throw new mops.businesslogic.exception.FileNotFoundException(message, e);
        }
        try {
            zipOutputStream.closeEntry();
        } catch (IOException e) {
            log.error("Failed to close zip entry for '{}", fileName);
            throw new MopsZipsException(String.format("Der zip konnte für '%s' nicht beendet werden", fileName), e);
        }
    }
}
