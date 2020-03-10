package mops.presentation;

import lombok.AllArgsConstructor;
import mops.businesslogic.Account;
import mops.businesslogic.FileService;
import mops.businesslogic.utils.AccountUtil;
import mops.persistence.file.FileInfo;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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
    @GetMapping("/{fileId}")
    public String getFile(KeycloakAuthenticationToken token,
                          Model model,
                          @PathVariable("fileId") long fileId) {
        Account account = AccountUtil.getAccountFromToken(token);
        FileInfo file = fileService.getFile(account, fileId);
        model.addAttribute("file", file);
        return "file";
    }

    /**
     * Deletes a file.
     *
     * @param token  keycloak auth token
     * @param model  spring view model
     * @param fileId the id of the file to be deleted
     * @return the route to the template 'file'
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
