package mops.presentation;

import lombok.AllArgsConstructor;
import mops.businesslogic.Account;
import mops.businesslogic.DirectoryService;
import mops.businesslogic.FileService;
import mops.businesslogic.utils.AccountUtil;
import mops.persistence.Directory;
import mops.persistence.FileInfo;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/material1/dir")
@AllArgsConstructor
public class DirectoryController {
    /**
     * Manages all directory queries.
     */
    private final DirectoryService directoryService;

    /**
     * Manges all file queries.
     */
    private final FileService fileService;

    /**
     * @param token keycloak auth token
     * @param model spring view model
     * @param dirId id of the folder
     * @return route to folder
     */
    @GetMapping(path = "/{dirId}")
    public String showFolderContent(KeycloakAuthenticationToken token,
                                    Model model,
                                    @PathVariable("dirId") int dirId) {
        final Account account = AccountUtil.getAccountFromToken(token);
        final List<Directory> directories = directoryService.getSubFolders(account, dirId);
        final List<FileInfo> files = fileService.getFilesOfDirectory(account, dirId);
        model.addAttribute("dirs", directories);
        model.addAttribute("files", files);
        return "directory";
    }

    /**
     * Uploads a file.
     *
     * @param token    keycloak auth token
     * @param model    spring view model
     * @param dirId    id of the directory id where it will be uploaded
     * @param fileInfo file object
     * @return route after completion
     */
    @PostMapping(path = "/{dirId}/upload")
    public String uploadFile(KeycloakAuthenticationToken token,
                             Model model,
                             @PathVariable("dirId") int dirId,
                             @Param("file") FileInfo fileInfo) {
        final Account account = AccountUtil.getAccountFromToken(token);
        //TODO: exception handling and user error message
        directoryService.uploadFile(account, dirId, fileInfo);
        return String.format("redirect:/material1/dir/%d", dirId);
    }

    /**
     * Creates a new sub folder.
     *
     * @param token      keycloak auth token
     * @param model      spring view model
     * @param dirId      id of the parent folder
     * @param folderName name of the new sub folder
     * @return object of the folder
     */
    @PostMapping("/{dirId}/create")
    public String createSubFolder(KeycloakAuthenticationToken token,
                                  Model model,
                                  @PathVariable("dirId") int dirId,
                                  @RequestAttribute("folderName") String folderName) {
        final Account account = AccountUtil.getAccountFromToken(token);
        final int directoryId = directoryService.createFolder(account, dirId, folderName);
        return String.format("redirect:/material1/dir/%d", directoryId);
    }

}

