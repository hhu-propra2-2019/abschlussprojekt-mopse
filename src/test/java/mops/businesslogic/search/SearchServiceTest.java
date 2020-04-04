package mops.businesslogic.search;

import mops.businesslogic.directory.DirectoryService;
import mops.businesslogic.exception.ReadAccessPermissionException;
import mops.businesslogic.file.FileListEntry;
import mops.businesslogic.file.query.FileQuery;
import mops.businesslogic.group.GroupService;
import mops.businesslogic.security.Account;
import mops.exception.MopsException;
import mops.persistence.FileInfoRepository;
import mops.persistence.FileRepository;
import mops.persistence.directory.Directory;
import mops.persistence.file.FileInfo;
import mops.persistence.group.Group;
import mops.persistence.permission.DirectoryPermissions;
import mops.util.DbContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;

@DbContext
@SpringBootTest
class SearchServiceTest {

    static final String STUDENTIN = "studentin";
    static final String ADMIN = "admin";
    static final String VIEWER = "viewer";
    static final String INTRUDER = "intruder";
    static final long GROUP_ID = 1L;
    static final UUID GROUP_UUID = new UUID(0, 1L);

    @MockBean
    GroupService groupService;
    @MockBean
    FileRepository fileRepository;

    @Autowired
    SearchService searchService;
    @Autowired
    DirectoryService directoryService;
    @Autowired
    FileInfoRepository fileInfoRepository;

    Directory root;
    Account admin;
    Account user;
    Account intruder;

    /**
     * Prepares user accounts.
     */
    @BeforeEach
    void setup() throws MopsException {
        intruder = Account.of(INTRUDER, "intruder@uni-koeln.de", STUDENTIN);
        user = Account.of(VIEWER, "user@hhu.de", STUDENTIN);
        admin = Account.of(ADMIN, "admin@hhu.de", STUDENTIN);

        Group group = Group.builder()
                .id(GROUP_ID)
                .groupId(GROUP_UUID)
                .name("Test Group")
                .member(admin.getName(), ADMIN)
                .member(user.getName(), VIEWER)
                .build();

        given(groupService.getGroup(GROUP_ID)).willReturn(group);
        given(groupService.getDefaultPermissions(GROUP_ID)).willReturn(
                DirectoryPermissions.builder()
                        .entry(ADMIN, true, true, true)
                        .entry(VIEWER, true, false, false)
                        .build()
        );

        root = directoryService.getOrCreateRootFolder(GROUP_ID).getRootDir();
    }


    @Test
    void searchFolderTest() throws MopsException {
        FileQuery query = FileQuery.builder()
                .name("a")
                .build();

        FileInfo matchingFile1 = fileInfoRepository.save(
                FileInfo.builder()
                        .name("a")
                        .directory(root)
                        .type("txt")
                        .size(0L)
                        .owner(VIEWER)
                        .build()
        );

        Directory child = directoryService.createFolder(admin, root.getId(), "child");

        FileInfo matchingFile2 = fileInfoRepository.save(
                FileInfo.builder()
                        .name("ab")
                        .directory(child)
                        .type("txt")
                        .size(0L)
                        .owner(VIEWER)
                        .build()
        );

        fileInfoRepository.save(
                FileInfo.builder()
                        .name("b")
                        .directory(root)
                        .type("txt")
                        .size(0L)
                        .owner(VIEWER)
                        .build()
        );

        List<FileListEntry> fileListEntries = searchService.searchFolder(user, root.getId(), query);
        List<FileInfo> fileInfos = fileListEntries.stream()
                .map(FileListEntry::getFileInfo)
                .collect(Collectors.toList());

        assertThat(fileInfos).containsExactlyInAnyOrder(matchingFile1, matchingFile2);
    }

    @Test
    void searchFolderWithoutPermissionTest() {
        FileQuery fileQuery = FileQuery.builder()
                .build();

        assertThatExceptionOfType(ReadAccessPermissionException.class)
                .isThrownBy(() -> searchService.searchFolder(intruder, root.getId(), fileQuery));
    }
}
