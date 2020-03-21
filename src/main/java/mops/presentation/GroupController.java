package mops.presentation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mops.businesslogic.directory.DirectoryService;
import mops.businesslogic.file.query.FileQuery;
import mops.businesslogic.group.GroupRootDirWrapper;
import mops.businesslogic.group.GroupService;
import mops.businesslogic.security.Account;
import mops.exception.MopsException;
import mops.persistence.file.FileInfo;
import mops.presentation.error.ExceptionPresentationError;
import mops.presentation.form.FileQueryForm;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

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
    private GroupService groupService;
    /**
     * For searching.
     */
    private DirectoryService directoryService;

    /**
     * Gets the root directory.
     *
     * @param redirectAttributes redirect attributes
     * @param groupId            the id of the group which files should be fetched
     * @return redirect to root dir
     */
    @GetMapping("/{groupId}")
    public String getRootDirectory(RedirectAttributes redirectAttributes,
                                   @PathVariable("groupId") long groupId) {
        log.info("Root directory of group with id '{}' requested.", groupId);

        try {
            GroupRootDirWrapper groupRootDir = groupService.getGroupUrl(groupId);
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
     * @param groupId the id of the group of the requested url
     * @return a wrapper for the url string
     */
    @GetMapping(value = "/{groupId}/url", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Secured("ROLE_api_user")
    public GroupRootDirWrapper getRootDirectoryUrl(@PathVariable("groupId") long groupId) {
        log.info("Group root directory url for group with id '{}' requested.", groupId);

        try {
            return groupService.getGroupUrl(groupId);
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
     * @param model              spring view model
     * @param groupId            the id of the group to be searched
     * @param queryForm          wrapper for a search query
     * @return the route to the template 'directory'
     */
    @PostMapping("/{groupId}/search")
    public String searchFilesInGroup(RedirectAttributes redirectAttributes,
                                     KeycloakAuthenticationToken token,
                                     Model model,
                                     @PathVariable("groupId") long groupId,
                                     @RequestAttribute("fileQueryForm") FileQueryForm queryForm) {
        Account account = Account.of(token);
        log.info("Search files in group with id '{}' requested by user '{}'.", groupId, account.getName());

        FileQuery query = queryForm.toQuery();

        try {
            List<FileInfo> files = new ArrayList<>(directoryService.searchFolder(account, groupId, query));
            model.addAttribute("files", files);
        } catch (MopsException e) {
            log.error("Failed to search for files in group with id '{}':", groupId, e);
            redirectAttributes.addFlashAttribute("error", new ExceptionPresentationError(e));
            return "redirect:/material1/error";
        }

        model.addAttribute("fileQueryForm", queryForm);
        model.addAttribute("account", account);
        return "files";
    }
}
