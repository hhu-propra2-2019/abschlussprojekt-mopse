package mops.presentation;

import lombok.AllArgsConstructor;
import mops.businesslogic.Account;
import mops.businesslogic.DirectoryService;
import mops.businesslogic.FileQuery;
import mops.businesslogic.FileService;
import mops.businesslogic.utils.AccountUtil;
import mops.exception.MopsException;
import mops.persistence.directory.Directory;
import mops.persistence.file.FileInfo;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

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
    @GetMapping("/{dirId}")
    @SuppressWarnings({ "PMD.DataflowAnomalyAnalysis", "PMD.EmptyCatchBlock" })
    public String showFolderContent(KeycloakAuthenticationToken token,
                                    Model model,
                                    @PathVariable("dirId") long dirId) {
        Account account = AccountUtil.getAccountFromToken(token);
        List<Directory> directories = null;
        List<FileInfo> files = null;
        try {
            directories = directoryService.getSubFolders(account, dirId);
            files = fileService.getFilesOfDirectory(account, dirId);
        } catch (MopsException e) {
            // TODO: Add exception handling, remove PMD warning suppression
        }
        model.addAttribute("dirs", directories);
        model.addAttribute("files", files);
        return "directory";
    }

    /**
     * Uploads a file.
     *
     * @param token         keycloak auth token
     * @param model         spring view model
     * @param dirId         id of the directory id where it will be uploaded
     * @param multipartFile file object
     * @return route after completion
     */
    @PostMapping("/{dirId}/upload")
    @SuppressWarnings({ "PMD.DataflowAnomalyAnalysis", "PMD.EmptyCatchBlock" })
    public String uploadFile(KeycloakAuthenticationToken token,
                             Model model,
                             @PathVariable("dirId") long dirId,
                             @RequestAttribute("file") MultipartFile multipartFile) {
        Account account = AccountUtil.getAccountFromToken(token);
        try {
            fileService.uploadFile(account, dirId, multipartFile, Set.of());
        } catch (MopsException e) {
            // TODO: Add exception handling, remove PMD warning suppression
        }
        return String.format("redirect:/material1/dir/%d", dirId);
    }

    /**
     * Creates a new sub folder.
     *
     * @param token       keycloak auth token
     * @param model       spring view model
     * @param parentDirId id of the parent folder
     * @param folderName  name of the new sub folder
     * @return object of the folder
     */
    @PostMapping("/{parentDirId}/create")
    public String createSubFolder(KeycloakAuthenticationToken token,
                                  Model model,
                                  @PathVariable("parentDirId") long parentDirId,
                                  @RequestAttribute("folderName") String folderName) {
        Account account = AccountUtil.getAccountFromToken(token);
        Directory directory = null;
        try {
            directory = directoryService.createFolder(account, parentDirId, folderName);
        } catch (MopsException e) {
            // TODO: Add exception handling, remove PMD warning suppression
        }
        return String.format("redirect:/material1/dir/%d", directory.getId());
    }

    /**
     * Deletes a folder.
     *
     * @param token user credentials
     * @param model spring view model
     * @param dirId id of the folder to be deleted
     * @return the id of the parent folder
     */
    @DeleteMapping("/{dirId}")
    public String deleteFolder(KeycloakAuthenticationToken token,
                               Model model,
                               @PathVariable("dirId") long dirId) {
        Account account = AccountUtil.getAccountFromToken(token);
        Directory directory = null;
        try {
            directory = directoryService.deleteFolder(account, dirId);
        } catch (MopsException e) {
            // TODO: Add exception handling, remove PMD warning suppression
        }
        return String.format("redirect:/material1/dir/%d", directory.getId());
    }

    /**
     * Searches a folder for files.
     *
     * @param token user credentials
     * @param model spring view model
     * @param dirId id of the folder to be searched
     * @param query wrapper object of the query parameter
     * @return route to files view
     */
    @PostMapping("/{dirId}/search")
    @SuppressWarnings({ "PMD.DataflowAnomalyAnalysis", "PMD.EmptyCatchBlock" })
    public String searchFolder(KeycloakAuthenticationToken token,
                               Model model,
                               @PathVariable("dirId") long dirId,
                               @RequestAttribute("searchQuery") FileQuery query) {
        Account account = AccountUtil.getAccountFromToken(token);
        List<FileInfo> files = null;
        try {
            files = directoryService.searchFolder(account, dirId, query);
        } catch (MopsException e) {
            // TODO: Add exception handling, remove PMD warning suppression
        }
        model.addAttribute("files", files);
        return "files";
    }
}

