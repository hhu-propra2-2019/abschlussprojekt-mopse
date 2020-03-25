package mops.presentation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mops.businesslogic.directory.DirectoryService;
import mops.businesslogic.group.GroupRootDirWrapper;
import mops.businesslogic.security.Account;
import mops.exception.MopsException;
import mops.presentation.error.ExceptionPresentationError;
import mops.presentation.form.FileQueryForm;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

/**
 * Controller Class for all requests on 'material1/group'.
 */
@Controller
@RequestMapping("material1/group")
@AllArgsConstructor
@Slf4j
// demeter violations in logging
// dataflow/one return violations in try-catch statements
@SuppressWarnings({ "PMD.DataflowAnomalyAnalysis", "PMD.OnlyOneReturn", "PMD.LawOfDemeter" })
public class GroupController {

    /**
     * Communicator for directory objects.
     */
    private DirectoryService directoryService;

    /**
     * Gets the root directory.
     *
     * @param redirectAttributes redirect attributes
     * @param token              keycloak auth token
     * @param groupId            the id of the group which files should be fetched
     * @return redirect to root dir
     */
    @GetMapping("/{groupId}")
    public String getRootDirectory(RedirectAttributes redirectAttributes,
                                   KeycloakAuthenticationToken token,
                                   @PathVariable("groupId") UUID groupId) {
        Account account = Account.of(token);
        log.info("Root directory of group with id '{}' requested by user '{}'.", groupId, account.getName());

        try {
            GroupRootDirWrapper groupRootDir = directoryService.getOrCreateRootFolder(groupId);
            return "redirect:" + groupRootDir.getRootDirUrl();
        } catch (MopsException e) {
            log.error("Failed to retrieve root directory for group with id '{}':", groupId, e);
            redirectAttributes.addFlashAttribute("error", new ExceptionPresentationError(e));
            return "redirect:/material1/error";
        }
    }

    /**
     * Gets the url of the root directory.
     *
     * @param token   keycloak auth token
     * @param groupId the id of the group of the requested url
     * @return a wrapper for the url string
     */
    @GetMapping(value = "/{groupId}/url", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Secured("ROLE_api_user")
    public GroupRootDirWrapper getRootDirectoryUrl(KeycloakAuthenticationToken token,
                                                   @PathVariable("groupId") UUID groupId) {
        Account account = Account.of(token);
        log.info("Group root directory url for group with id '{}' requested by user '{}'.", groupId, account.getName());

        try {
            return directoryService.getOrCreateRootFolder(groupId);
        } catch (MopsException e) {
            log.error("Failed to retrieve group root directory url for group with id '{}':", groupId, e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Error while retrieving group root directory.", e);
        }
    }

    /**
     * Searches are group for matching files.
     *
     * @param redirectAttributes redirect attributes
     * @param token              keycloak auth token
     * @param groupId            the id of the group to be searched
     * @param queryForm          wrapper for a search query
     * @return the route to the template 'directory'
     */
    @PostMapping("/{groupId}/search")
    public String searchFilesInGroup(RedirectAttributes redirectAttributes,
                                     KeycloakAuthenticationToken token,
                                     @PathVariable("groupId") UUID groupId,
                                     @ModelAttribute("fileQueryForm") FileQueryForm queryForm) {
        Account account = Account.of(token);
        log.info("Search files in group with id '{}' requested by user '{}'.", groupId, account.getName());

        try {
            GroupRootDirWrapper groupRootDir = directoryService.getOrCreateRootFolder(groupId);
            redirectAttributes.addFlashAttribute("fileQueryForm", queryForm);
            return "redirect:" + groupRootDir.getRootDirUrl() + "/search";
        } catch (MopsException e) {
            log.error("Failed to search for files in group with id '{}':", groupId, e);
            redirectAttributes.addFlashAttribute("error", new ExceptionPresentationError(e));
            return "redirect:/material1/error";
        }
    }
}
