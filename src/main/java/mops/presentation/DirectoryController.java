package mops.presentation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mops.businesslogic.directory.DeleteService;
import mops.businesslogic.directory.DirectoryService;
import mops.businesslogic.directory.ZipService;
import mops.businesslogic.file.FileService;
import mops.businesslogic.file.query.FileQuery;
import mops.businesslogic.permission.PermissionService;
import mops.businesslogic.security.Account;
import mops.businesslogic.security.SecurityService;
import mops.businesslogic.security.UserPermission;
import mops.exception.MopsException;
import mops.persistence.directory.Directory;
import mops.persistence.file.FileInfo;
import mops.persistence.permission.DirectoryPermissions;
import mops.presentation.error.ExceptionPresentationError;
import mops.presentation.form.EditDirectoryForm;
import mops.presentation.form.FileQueryForm;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Set;

/**
 * Controller Class for all requests on 'material1/dir'.
 */
@Controller
@RequestMapping("/material1/dir")
@AllArgsConstructor
@Slf4j
// demeter violations in logging
// dataflow/one return violations in try-catch statements
@SuppressWarnings({ "PMD.DataflowAnomalyAnalysis", "PMD.OnlyOneReturn", "PMD.LawOfDemeter", "PMD.ExcessiveImports" })
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
     * Fetches permissions of user.
     */
    private final SecurityService securityService;
    /**
     * Fetches role permissions.
     */
    private final PermissionService permissionService;
    /**
     * Deletes files and directories.
     */
    private final DeleteService deleteService;
    /**
     * Zips a directory.
     */
    private final ZipService zipService;

    /**
     * Shows the content of a folder (files and sub folders).
     *
     * @param redirectAttributes redirect attributes
     * @param token              keycloak auth token
     * @param model              spring view model
     * @param dirId              id of the folder
     * @return route to folder
     */
    @GetMapping("/{dirId}")
    public String showFolderContent(RedirectAttributes redirectAttributes,
                                    KeycloakAuthenticationToken token,
                                    Model model,
                                    @PathVariable("dirId") long dirId) {
        Account account = Account.of(token);
        log.info("Folder content for folder with id '{}' requested by user '{}'.", dirId, account.getName());

        try {
            Directory directory = directoryService.getDirectory(dirId);
            List<Directory> directories = directoryService.getSubFolders(account, dirId);
            List<FileInfo> files = fileService.getFilesOfDirectory(account, dirId);
            UserPermission userPermission = securityService.getPermissionsOfUser(account, directory);
            List<Directory> dirPath = directoryService.getDirectoryPath(dirId);
            boolean admin = securityService.isUserAdmin(account, directory.getGroupOwner());
            DirectoryPermissions permissions = permissionService.getPermissions(directory);
            EditDirectoryForm editDirectoryForm = EditDirectoryForm.of(directory, permissions);

            model.addAttribute("deletePermission", userPermission.isDelete());
            model.addAttribute("writePermission", userPermission.isWrite());
            model.addAttribute("adminRole", admin);
            model.addAttribute("directory", directory);
            model.addAttribute("dirs", directories);
            model.addAttribute("files", files);
            model.addAttribute("directoryPath", dirPath);
            model.addAttribute("editDirectoryForm", editDirectoryForm);
        } catch (MopsException e) {
            log.error("Failed to retrieve the folder content for directory with id '{}':", dirId, e);
            redirectAttributes.addFlashAttribute("error", new ExceptionPresentationError(e));
            return "redirect:/material1/error";
        }

        model.addAttribute("fileQueryForm", new FileQueryForm());
        model.addAttribute("account", account);
        return "overview";
    }

    /**
     * Uploads a file.
     *
     * @param redirectAttributes redirect attributes
     * @param token              keycloak auth token
     * @param dirId              id of the directory where it will be uploaded
     * @param multipartFile      file object
     * @return route after completion
     */
    @PostMapping("/{dirId}/upload")
    public String uploadFile(RedirectAttributes redirectAttributes,
                             KeycloakAuthenticationToken token,
                             @PathVariable("dirId") long dirId,
                             @RequestParam("file") MultipartFile multipartFile) {
        Account account = Account.of(token);
        log.info("Upload of a file in directory with id '{}' requested by user '{}'.", dirId, account.getName());

        try {
            // TODO: get tags from html form
            fileService.saveFile(account, dirId, multipartFile, Set.of());
        } catch (MopsException e) {
            log.error("Failed to upload file in directory with id '{}':", dirId, e);
            redirectAttributes.addFlashAttribute("error", new ExceptionPresentationError(e));
            return "redirect:/material1/error";
        }

        redirectAttributes.addAttribute("dirId", dirId);
        return "redirect:/material1/dir/{dirId}";
    }

    /**
     * Download a directory as zip.
     *
     * @param token keycloak auth token
     * @param dirId id of the directory
     * @return a zip as byte array
     */
    @GetMapping("/{dirId}/zip")
    public ResponseEntity<byte[]> zipDirectory(KeycloakAuthenticationToken token,
                                               @PathVariable("dirId") long dirId) {
        Account account = Account.of(token);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Directory directory;
        try {
            zipService.zipDirectory(account, dirId, bos);
            directory = directoryService.getDirectory(dirId);
        } catch (MopsException e) {
            log.error("Failed to zip directory with id: {}", dirId);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Ordner konnte nicht gezippt werden.", e);
        }
        byte[] bytes = bos.toByteArray();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(bytes.length)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        String.format("attachment; filename=\"%s.zip\"", directory.getName()))
                .body(bytes);
    }

    /**
     * Creates a new sub folder.
     *
     * @param redirectAttributes redirect attributes
     * @param token              keycloak auth token
     * @param parentDirId        id of the parent folder
     * @param folderName         name of the new sub folder
     * @return object of the folder
     */
    @PostMapping("/{parentDirId}/create")
    public String createSubFolder(RedirectAttributes redirectAttributes,
                                  KeycloakAuthenticationToken token,
                                  @PathVariable("parentDirId") long parentDirId,
                                  @RequestParam("folderName") String folderName) {
        Account account = Account.of(token);
        log.info("Sub folder creation requested in parent folder with id '{}' by user '{}'.",
                parentDirId, account.getName());

        try {
            Directory directory = directoryService.createFolder(account, parentDirId, folderName);
            redirectAttributes.addAttribute("dirId", directory.getId());
        } catch (MopsException e) {
            log.error("Failed to create folder in parent directory with id '{}':", parentDirId, e);
            redirectAttributes.addFlashAttribute("error", new ExceptionPresentationError(e));
            return "redirect:/material1/error";
        }

        return "redirect:/material1/dir/{dirId}";
    }

    /**
     * Edit the folder.
     *
     * @param redirectAttributes redirect attributes
     * @param token              keycloak auth token
     * @param dirId              id of the current folder
     * @param editForm           new directory properties from the template
     * @return object of the folder
     */
    @PostMapping("/{dirId}/edit")
    public String editFolder(RedirectAttributes redirectAttributes,
                             KeycloakAuthenticationToken token,
                             @PathVariable("dirId") long dirId,
                             @ModelAttribute("editDirectoryForm") EditDirectoryForm editForm) {
        Account account = Account.of(token);
        log.info("Directory edit requested in directory with id '{}' by user '{}'.", dirId, account.getName());

        String newName = editForm.getName();
        DirectoryPermissions newPermissions = editForm.buildDirectoryPermissions();

        try {
            Directory directory = directoryService.editDirectory(account, dirId, newName, newPermissions);
            redirectAttributes.addAttribute("dirId", directory.getId());
        } catch (MopsException e) {
            log.error("Failed to edit directory with id '{}':", dirId, e);
            redirectAttributes.addFlashAttribute("error", new ExceptionPresentationError(e));
            return "redirect:/material1/error";
        }

        return "redirect:/material1/dir/{dirId}";
    }

    /**
     * Deletes a folder.
     *
     * @param redirectAttributes redirect attributes
     * @param token              user credentials
     * @param dirId              id of the folder to be deleted
     * @return the id of the parent folder
     */
    @PostMapping("/{dirId}/delete")
    public String deleteFolder(RedirectAttributes redirectAttributes,
                               KeycloakAuthenticationToken token,
                               @PathVariable("dirId") long dirId) {
        Account account = Account.of(token);
        log.info("Deletion of folder with id {} requested by user '{}'.", dirId, account.getName());

        try {
            Directory directory = deleteService.deleteFolder(account, dirId);
            if (directory == null) {
                return "redirect:/material1/groups";
            }
            redirectAttributes.addAttribute("dirId", directory.getId());
        } catch (MopsException e) {
            log.error("Failed to delete folder with id '{}':", dirId, e);
            redirectAttributes.addFlashAttribute("error", new ExceptionPresentationError(e));
            return "redirect:/material1/error";
        }

        return "redirect:/material1/dir/{dirId}";
    }

    /**
     * Searches a folder for files.
     *
     * @param redirectAttributes redirect attributes
     * @param token              user credentials
     * @param model              spring view model
     * @param dirId              id of the folder to be searched
     * @param queryForm          wrapper object of the query form parameter
     * @return route to files view
     */
    @PostMapping("/{dirId}/search")
    public String searchFolder(RedirectAttributes redirectAttributes,
                               KeycloakAuthenticationToken token,
                               Model model,
                               @PathVariable("dirId") long dirId,
                               @ModelAttribute("fileQueryForm") FileQueryForm queryForm) {
        Account account = Account.of(token);
        log.info("Search for file in the folder with the id '{}' requested by user '{}'.", dirId, account.getName());

        FileQuery query = queryForm.toQuery();

        try {
            List<FileInfo> files = directoryService.searchFolder(account, dirId, query);
            Directory directory = directoryService.getDirectory(dirId);
            model.addAttribute("directory", directory);
            model.addAttribute("files", files);
        } catch (MopsException e) {
            log.error("Failed to search in folder with id '{}':", dirId, e);
            redirectAttributes.addFlashAttribute("error", new ExceptionPresentationError(e));
            return "redirect:/material1/error";
        }

        model.addAttribute("fileQueryForm", queryForm);
        model.addAttribute("account", account);
        model.addAttribute("backDirId", dirId);
        // always show delete
        model.addAttribute("deletePermission", true);
        return "overview";
    }
}
