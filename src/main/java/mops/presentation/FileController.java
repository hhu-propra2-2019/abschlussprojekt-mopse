package mops.presentation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mops.businesslogic.file.FileContainer;
import mops.businesslogic.file.FileService;
import mops.businesslogic.security.Account;
import mops.exception.MopsException;
import mops.persistence.directory.Directory;
import mops.presentation.error.ExceptionPresentationError;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller Class for all requests on 'material1/file'.
 */
@Controller
@RequestMapping("material1/file")
@AllArgsConstructor
@Slf4j
// demeter violations in logging
// dataflow/one return violations in try-catch statements
@SuppressWarnings({ "PMD.DataflowAnomalyAnalysis", "PMD.OnlyOneReturn", "PMD.LawOfDemeter" })
public class FileController {

    /**
     * Handles actions on single files: Delete, Preview, Download.
     */
    private final FileService fileService;

    /**
     * Retrieves a file preview.
     *
     * @param token  user credentials
     * @param fileId the id of the requested file
     * @return in-browser preview of the file
     */
    @GetMapping("/{fileId}")
    public ResponseEntity<Resource> showFile(KeycloakAuthenticationToken token,
                                             @PathVariable("fileId") long fileId) {
        Account account = Account.of(token);
        log.info("File with id '{}' requested for download by user '{}'.", fileId, account.getName());

        FileContainer result;
        try {
            result = fileService.getFile(account, fileId);
        } catch (MopsException e) {
            log.error("Failed to retrieve file with id '{}':", fileId, e);
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "The requested file could not be found.", e);
        }

        MediaType contentType = MediaType.parseMediaType(result.getType());
        long contentLength = result.getSize();

        return ResponseEntity.ok()
                .contentType(contentType)
                .contentLength(contentLength)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + result.getName() + "\"")
                .body(result.getContent());
    }

    /**
     * Downloads a file.
     *
     * @param token  a keycloak authentication token
     * @param fileId the id of the requested file
     * @return the route to template 'file'
     */
    @ResponseBody
    @GetMapping("/{fileId}/download")
    public ResponseEntity<Resource> downloadFile(KeycloakAuthenticationToken token,
                                                 @PathVariable("fileId") long fileId) {
        Account account = Account.of(token);
        log.info("File with id '{}' requested for download by user '{}'.", fileId, account.getName());

        FileContainer result;
        try {
            result = fileService.getFile(account, fileId);
        } catch (MopsException e) {
            log.error("Failed to retrieve file with id '{}':", fileId, e);
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "The requested file could not be found.", e);
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
     * @param redirectAttributes redirect attributes
     * @param token              keycloak auth token
     * @param fileId             the id of the file to be deleted
     * @return the route to the parentDir of the deleted file
     */
    @PostMapping("/{fileId}/delete")
    public String deleteFile(RedirectAttributes redirectAttributes,
                             KeycloakAuthenticationToken token,
                             @PathVariable("fileId") long fileId) {
        Account account = Account.of(token);
        log.info("Deletion of file with id '{}' requested by user '{}'.", fileId, account.getName());

        try {
            Directory dir = fileService.deleteFile(account, fileId);
            redirectAttributes.addAttribute("dirId", dir.getId());
            return "redirect:/material1/dir/{dirId}";
        } catch (MopsException e) {
            log.error("Failed to delete file with id '{}':", fileId, e);
            redirectAttributes.addFlashAttribute("error", new ExceptionPresentationError(e));
            return "redirect:/material1/error";
        }
    }

    /**
     * Renames a file.
     *
     * @param redirectAttributes    redirection attributes
     * @param token                 keyloak auth token
     * @param fileId                the file id
     * @param newName               the new name
     * @return                      back to the overview
     */
    @PostMapping("/{fileId}/rename")
    public String renameFile(RedirectAttributes redirectAttributes,
                             KeycloakAuthenticationToken token,
                             @PathVariable("fileId") long fileId,
                             @RequestParam("newName") String newName) {
        System.err.println("WTF");
        Account account = Account.of(token);
        try {
            Directory dir = fileService.renameFile(account, fileId, newName);
            redirectAttributes.addAttribute("dirId", dir.getId());
            return "redirect:/material1/dir/{dirId}";
        } catch (MopsException e) {
            log.error("Failed to rename file with id '{}':", fileId, e);
            redirectAttributes.addFlashAttribute("error", new ExceptionPresentationError(e));
            return "redirect:/material1/error";
        }
    }
}
