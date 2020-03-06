package mops.presentation;

import lombok.AllArgsConstructor;
import mops.businesslogic.FileService;
import mops.persistence.FileInfo;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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
     * @param token   a keycloak authentication token
     * @param model   spring boot view model
     * @param groupId the id of the group which files should be fetched
     * @return the route to template 'directory'
     */
    @GetMapping(path = "/{groupId}")
    public String getAllFilesOfDirectory(KeycloakAuthenticationToken token,
                                         Model model,
                                         @PathVariable("groupId") int groupId) {
        final List<FileInfo> files = fileService.getAllFilesOfGroup(groupId);
        model.addAttribute("files", files);
        return "directory";
    }
}
