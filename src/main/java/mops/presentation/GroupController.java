package mops.presentation;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mops.businesslogic.Account;
import mops.businesslogic.DirectoryService;
import mops.businesslogic.GroupRootDirWrapper;
import mops.businesslogic.GroupService;
import mops.businesslogic.query.FileQuery;
import mops.businesslogic.utils.AccountUtil;
import mops.exception.MopsException;
import mops.persistence.file.FileInfo;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("material1/group")
@AllArgsConstructor
@Slf4j
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
     * @param token   a keycloak authentication token
     * @param model   spring boot view model
     * @param groupId the id of the group which files should be fetched
     * @return redirect to root dir
     */
    @GetMapping("/{groupId}")
    @SuppressWarnings({ "PMD.DataflowAnomalyAnalysis", "PMD.EmptyCatchBlock", "PMD.LawOfDemeter" })
    @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_EXCEPTION", justification = "Remove with exception handling")
    public String getRootDirectory(KeycloakAuthenticationToken token,
                                   Model model,
                                   @PathVariable("groupId") long groupId) {
        log.info("Root directory of group with id {} requested.", groupId);
        Account account = AccountUtil.getAccountFromToken(token);
        GroupRootDirWrapper groupRootDir = null;
        try {
            groupRootDir = groupService.getGroupUrl(account, groupId);
        } catch (MopsException e) {
            // TODO: Add exception handling, remove PMD warning suppression
            log.error("Failed to retrieve root directory for group with id '{}'", groupId);
        }
        return String.format("redirect:%s", groupRootDir.getRootDirUrl()); // no demeter violation here
    }

    /**
     * @param token   a keycloak authentication token
     * @param model   spring boot view model
     * @param groupId the id of the group of the requested url
     * @return a wrapper for the url string
     */
    @GetMapping(value = "/{groupId}/url", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Secured("ROLE_api_user")
    @SuppressWarnings({ "PMD.DataflowAnomalyAnalysis", "PMD.EmptyCatchBlock" })
    public GroupRootDirWrapper getGroupUrl(KeycloakAuthenticationToken token,
                                           Model model,
                                           @PathVariable("groupId") long groupId) {
        log.info("Group url for group with id '{}' requested.", groupId);
        Account account = AccountUtil.getAccountFromToken(token);
        GroupRootDirWrapper groupRootDir = null;
        try {
            groupRootDir = groupService.getGroupUrl(account, groupId);
        } catch (MopsException e) {
            // TODO: Add exception handling, remove PMD warning suppression
            log.error("Failed to retrieve group url for group with id '{}'.", groupId);
        }
        return groupRootDir;
    }

    /**
     * Searches are group for matching files.
     *
     * @param token   keycloak auth token
     * @param model   spring view model
     * @param groupId the id of the group to be searched
     * @param query   wrapper for a search query
     * @return the route to the template 'directory'
     */
    @PostMapping("/{groupId}/search")
    @SuppressWarnings({ "PMD.DataflowAnomalyAnalysis", "PMD.EmptyCatchBlock" })
    public String searchFilesInGroup(KeycloakAuthenticationToken token,
                                     Model model,
                                     @PathVariable("groupId") long groupId,
                                     @RequestAttribute("search") FileQuery query) {
        log.info("Search files in group with id '{}' requested.", groupId);
        Account account = AccountUtil.getAccountFromToken(token);
        List<FileInfo> files = null;
        try {
            files = directoryService.searchFolder(account, groupId, query);
        } catch (MopsException e) {
            // TODO: Add exception handling, remove PMD warning suppression
            log.error("Failed to search for files in group with id '{}'.", groupId);
        }
        model.addAttribute("files", files);
        return "files";
    }
}
