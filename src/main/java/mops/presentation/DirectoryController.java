package mops.presentation;

import lombok.AllArgsConstructor;
import mops.businesslogic.Account;
import mops.businesslogic.DirectoryService;
import mops.businesslogic.utils.AccountUtil;
import mops.persistence.FileInfo;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/material1/dir")
@AllArgsConstructor
public class DirectoryController {
    /**
     * Manages all directory queries.
     */
    private final DirectoryService directoryService;

    /**
     * Uploads a file.
     *
     * @param token    keycloak auth token
     * @param model    spring view model
     * @param groupId  id of the group where it will be uploaded
     * @param fileInfo file object
     * @return route after completion
     */
    @PostMapping(path = "/{groupId}/upload")
    public String uploadFile(KeycloakAuthenticationToken token,
                             Model model,
                             @PathVariable("groupId") int groupId,
                             @Param("file") FileInfo fileInfo) {
        Account account = AccountUtil.getAccountFromToken(token);
        //TODO: exception handling and user error message
        directoryService.uploadFile(account, groupId, fileInfo);
        return String.format("redirect:/material1/dir/%d", groupId);
    }
}

