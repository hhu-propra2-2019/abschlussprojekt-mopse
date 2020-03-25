package mops.businesslogic.config;

import lombok.extern.slf4j.Slf4j;
import mops.businesslogic.directory.DirectoryService;
import mops.businesslogic.file.FileInfoService;
import mops.businesslogic.group.GroupService;
import mops.businesslogic.security.Account;
import mops.persistence.FileRepository;
import mops.persistence.directory.Directory;
import mops.persistence.file.FileInfo;
import mops.persistence.group.Group;
import mops.persistence.permission.DirectoryPermissions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Setups data for development.
 */
@Slf4j
@Configuration
@Profile("dev")
public class DummyDataSeeding {

    /**
     * Represents the role of an admin.
     */
    @Value("${material1.mops.configuration.role.admin}")
    @SuppressWarnings({ "PMD.ImmutableField", "PMD.BeanMembersShouldSerialize" })
    private String adminRole = "admin";
    /**
     * Represents the role of a viewer.
     */
    @Value("${material1.mops.configuration.role.viewer}")
    @SuppressWarnings({ "PMD.ImmutableField", "PMD.BeanMembersShouldSerialize" })
    private String viewerRole = "viewer";

    /**
     * Initializes application runner.
     *
     * @param groupService     group service
     * @param fileInfoService  file info service
     * @param fileRepository   connection to the MinIO file repository
     * @param directoryService directory service
     * @return an ApplicationRunner
     */
    @Bean
    public ApplicationRunner init(GroupService groupService,
                                  FileInfoService fileInfoService,
                                  FileRepository fileRepository,
                                  DirectoryService directoryService) {
        return args -> {
            log.info("Seeding database with dummy data.");

            Group group = groupService.getAllGroups().get(0);
            final int fileSize1 = 2_000;
            final int fileSize2 = 3_000;
            final String owner1 = "studentin";
            final String owner2 = "studentin1";

            Account admin = Account.of("admin", "admin@hhu.de", "admin");

            DirectoryPermissions directoryPermissions = DirectoryPermissions.builder()
                    .entry(adminRole, true, true, true)
                    .entry(viewerRole, true, false, false)
                    .build();

            Directory directoryParent = directoryService.getOrCreateRootFolder(group.getId()).getRootDir();
            directoryService.updatePermission(admin, directoryParent.getId(), directoryPermissions);

            List<Directory> children = directoryService.getSubFolders(admin, directoryParent.getId());
            Directory directoryChild;

            if (children.isEmpty()) {
                directoryChild = directoryService.createFolder(admin, directoryParent.getId(), "Child Folder");
            } else {
                directoryChild = children.get(0);
            }

            List<FileInfo> filesInRootDir = fileInfoService.fetchAllFilesInDirectory(directoryParent.getId());
            FileInfo fileInfoParent;

            if (filesInRootDir.isEmpty()) {
                fileInfoParent = FileInfo.builder()
                        .name("Test1")
                        .directory(directoryParent)
                        .type("application/pdf")
                        .size(fileSize1)
                        .owner(owner1)
                        .tag("Test")
                        .build();
                fileInfoParent = fileInfoService.saveFileInfo(fileInfoParent);

                byte[] contentParent = new byte[fileSize1];
                try (InputStream stream = new ByteArrayInputStream(contentParent)) {
                    fileRepository.saveFile(stream, fileSize1, fileInfoParent.getType(), fileInfoParent.getId());
                }
            } else {
                fileInfoParent = filesInRootDir.get(0);
            }

            List<FileInfo> filesInChildDir = fileInfoService.fetchAllFilesInDirectory(directoryParent.getId());
            FileInfo fileInfoChild;

            if (filesInRootDir.isEmpty()) {
                fileInfoChild = FileInfo.builder()
                        .name("Test2")
                        .directory(directoryChild)
                        .type("image/png")
                        .size(fileSize2)
                        .owner(owner2)
                        .tag("Test")
                        .build();
                fileInfoChild = fileInfoService.saveFileInfo(fileInfoChild);

                byte[] contentChild = new byte[fileSize2];
                try (InputStream stream = new ByteArrayInputStream(contentChild)) {
                    fileRepository.saveFile(stream, fileSize2, fileInfoChild.getType(), fileInfoChild.getId());
                }
            } else {
                fileInfoChild = filesInChildDir.get(0);
            }
        };
    }
}
