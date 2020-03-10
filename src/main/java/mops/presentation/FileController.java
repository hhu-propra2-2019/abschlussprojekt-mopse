package mops.presentation;

import lombok.AllArgsConstructor;
import mops.businesslogic.Account;
import mops.businesslogic.FileService;
import mops.businesslogic.utils.AccountUtil;
import mops.persistence.file.FileInfo;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Controller
@RequestMapping("material1/file")
@AllArgsConstructor
public class FileController {

    /**
     * Handles actions on single files: Delete, Preview, Download.
     */
    private final FileService fileService;

    /**
     * @param token  a keycloak authentication token
     * @param model  spring boot view model
     * @param fileId the id of the requested file
     * @return the route to template 'file'
     */
    @ResponseBody
    @GetMapping("/{fileId}")
    ResponseEntity getFile(KeycloakAuthenticationToken token,
                                @PathVariable("fileId") long fileId) {
        Account account = AccountUtil.getAccountFromToken(token);
        FileInfo result = fileService.getFile(account, fileId);

        if (result.getId() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    // FORBIDDEN because elseway it would be obvious to the user if the file exists
                    "Zugriff auf Datei mit der ID "
                            + fileId
                            + " ist nicht erlaubt.");
        }

        MediaType contentType = MediaType.parseMediaType(result.getType());
        FileSystemResource resource = new FileSystemResource("material1/file/" + fileId);
        //TODO: We don't know what kind of Resource we get from FileService

        try {
            Long contentLength = resource.contentLength();

            return ResponseEntity.ok()
                    .contentType(contentType)
                    .contentLength(contentLength)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + result.getName() + "\"")
                    .body(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok()
                .contentType(contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + result.getName() + "\"")
                .body(resource);
    }

    /**
     * Deletes a file.
     *
     * @param token  keycloak auth token
     * @param model  spring view model
     * @param fileId the id of the file to be deleted
     * @return the route to the parentDir of the deleted file
     */
    @DeleteMapping("/{fileId}")
    public String deleteFile(KeycloakAuthenticationToken token,
                             Model model,
                             @PathVariable("fileId") long fileId) {
        Account account = AccountUtil.getAccountFromToken(token);
        long dirId = fileService.getFile(account, fileId).getDirectoryId();
        fileService.deleteFile(account, fileId);
        return String.format("redirect:/material1/dir/%d", dirId);
    }
}
