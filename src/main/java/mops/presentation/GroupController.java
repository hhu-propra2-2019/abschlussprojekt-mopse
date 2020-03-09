package mops.presentation;

import lombok.AllArgsConstructor;
import mops.businesslogic.*;
import mops.businesslogic.utils.AccountUtil;
import mops.persistence.FileInfo;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("material1/group")
@AllArgsConstructor
public class GroupController {

    /**
     * Retrieves file information for a folder.
     */
    private FileService fileService;

    /**
     * Communicator for directory objects.
     */
    private GroupService groupService;

    /**
     * @param token   a keycloak authentication token
     * @param model   spring boot view model
     * @param groupId the id of the group which files should be fetched
     * @return the route to template 'directory'
     */
    @GetMapping("/{groupId}")
    public String getAllFilesOfDirectory(KeycloakAuthenticationToken token,
                                         Model model,
                                         @PathVariable("groupId") long groupId) {
        Account account = AccountUtil.getAccountFromToken(token);
        List<FileInfo> files = fileService.getAllFilesOfGroup(account, groupId);
        model.addAttribute("files", files);
        return "files";
    }

    /**
     * @param token   a keycloak authentication token
     * @param model   spring boot view model
     * @param groupId the id of the group of the requested url
     * @return a wrapper for the url string
     */
    @GetMapping(value = "/{groupId}/url", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public GroupURLWrapper getGroupURL(KeycloakAuthenticationToken token,
                                       Model model,
                                       @PathVariable("groupId") long groupId) {
        Account account = AccountUtil.getAccountFromToken(token);
        return groupService.getGroupURL(account, groupId);
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
    public String searchFilesInGroup(KeycloakAuthenticationToken token,
                                     Model model,
                                     @PathVariable("groupId") long groupId,
                                     @ModelAttribute("searchQuery") FileQuery query) {
        Account account = AccountUtil.getAccountFromToken(token);
        List<FileInfo> files = fileService.searchFilesInGroup(account, groupId, query);
        model.addAttribute("files", files);
        return "files";
    }
}
