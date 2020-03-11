package mops.presentation;

import lombok.AllArgsConstructor;
import mops.businesslogic.Account;
import mops.businesslogic.FileContainer;
import mops.businesslogic.FileService;
import mops.businesslogic.utils.AccountUtil;
import mops.exception.MopsException;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
     * @param fileId the id of the requested file
     * @return the route to template 'file'
     */
    @SuppressWarnings("PMD")
    @ResponseBody
    @GetMapping("/{fileId}/download")
    ResponseEntity<Resource> getFile(KeycloakAuthenticationToken token,
                                     @PathVariable("fileId") long fileId) {
        Account account = AccountUtil.getAccountFromToken(token);
        FileContainer result;
        try {
            result = fileService.getFile(account, fileId);
        } catch (MopsException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Access to file with ID "
                            + fileId
                            + " is forbidden.");
        }

        MediaType contentType = MediaType.parseMediaType(result.getType());
        long contentLength = result.getSize();

        return ResponseEntity.ok()
                .contentType(contentType)
                .contentLength(contentLength)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + result.getName() + "\"")
                .body(result.getContent());
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
        long dirId = fileService.deleteFile(account, fileId);
        return String.format("redirect:/material1/dir/%d", dirId);
    }
}
