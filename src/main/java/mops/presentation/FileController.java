package mops.presentation;

import lombok.AllArgsConstructor;
import mops.businesslogic.Account;
import mops.businesslogic.FileContainer;
import mops.businesslogic.FileService;
import mops.businesslogic.utils.AccountUtil;
import mops.exception.MopsException;
import mops.persistence.directory.Directory;
import mops.persistence.file.FileInfo;
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

import javax.servlet.http.HttpServletRequest;

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
     * @param model  model
     * @param fileId the id of the requested file
     * @return the route to template 'file'
     */
    @GetMapping("/{fileId}")
    @SuppressWarnings({ "PMD.DataflowAnomalyAnalysis", "PMD.EmptyCatchBlock" })
    public String getFile(KeycloakAuthenticationToken token,
                          Model model,
                          @PathVariable("fileId") long fileId) {
        Account account = AccountUtil.getAccountFromToken(token);
        FileInfo info = null;
        try {
            info = fileService.getFileInfo(account, fileId);
        } catch (MopsException e) {
            // TODO: Add exception handling, remove PMD warning suppression
        }
        model.addAttribute("file", info);
        return "file";
    }

    /**
     * @param token  a keycloak authentication token
     * @param fileId the id of the requested file
     * @return the route to template 'file'
     */
    @ResponseBody
    @GetMapping("/{fileId}/download")
    @SuppressWarnings({ "PMD.LawOfDemeter", "PMD.DataflowAnomalyAnalysis" })
    public ResponseEntity<Resource> downloadFile(KeycloakAuthenticationToken token,
                                                 @PathVariable("fileId") long fileId) {
        Account account = AccountUtil.getAccountFromToken(token);
        FileContainer result;
        try {
            result = fileService.getFile(account, fileId);
        } catch (MopsException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Die Datei mit der ID " + fileId + " konnte nicht gefunden werden.", e);
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
     * @param token   keycloak auth token
     * @param model   spring view model
     * @param fileId  the id of the file to be deleted
     * @param request the Http request
     * @return the route to the parentDir of the deleted file
     */
    @DeleteMapping("/{fileId}")
    @SuppressWarnings({ "PMD.DataflowAnomalyAnalysis", "PMD.EmptyCatchBlock" })
    public String deleteFile(KeycloakAuthenticationToken token,
                             Model model,
                             @PathVariable("fileId") long fileId,
                             HttpServletRequest request) {
        Account account = AccountUtil.getAccountFromToken(token);
        Directory dir = null;
        String url;
        try {
            dir = fileService.deleteFile(account, fileId);
            url = String.format("redirect:/material1/dir/%d", dir.getId());
        } catch (MopsException e) {
            // TODO: Add exception handling, remove PMD warning suppression
            String referer = request.getHeader("Referer");
            url = "redirect:" + referer;
        }
        return url;
    }
}
