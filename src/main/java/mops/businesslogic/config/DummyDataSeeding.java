package mops.businesslogic.config;

import lombok.extern.slf4j.Slf4j;
import mops.businesslogic.directory.DirectoryService;
import mops.businesslogic.file.FileInfoService;
import mops.businesslogic.security.Account;
import mops.persistence.FileRepository;
import mops.persistence.directory.Directory;
import mops.persistence.file.FileInfo;
import mops.persistence.permission.DirectoryPermissions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Setups data for development.
 */
@Slf4j
@Configuration
@Profile("dev")
public class DummyDataSeeding {

    /**
     * Group id.
     */
    private static final long GROUP_ID = 100L;

    /**
     * Represents the role of an admin.
     */
    @Value("${material1.mops.configuration.admin}")
    @SuppressWarnings({ "PMD.ImmutableField", "PMD.BeanMembersShouldSerialize" })
    private String adminRole = "admin";

    /**
     * Initializes application runner.
     *
     * @param fileInfoService  file info service
     * @param fileRepository   connection to the MinIO file repository
     * @param directoryService directory service
     * @return an ApplicationRunner
     */
    @Bean
    public ApplicationRunner init(FileInfoService fileInfoService,
                                  FileRepository fileRepository,
                                  DirectoryService directoryService) {
        return args -> {
            log.info("Seeding database with dummy data.");

            final int fileSize1 = 2_000;
            final int fileSize2 = 3_000;
            final String owner1 = "studentin";
            final String owner2 = "studentin1";

            Account admin = Account.of("admin", "admin@hhu.de", "admin");

            DirectoryPermissions directoryPermissions = DirectoryPermissions.builder()
                    .entry(adminRole, true, true, true)
                    .entry("editor", true, true, false)
                    .entry("viewer", true, false, false)
                    .entry("editor", true, true, true)
                    .entry("korrektor", true, true, true)
                    .entry("viewer", true, true, true)
                    .entry("studentin", true, true, true)
                    .build();

            Directory directoryParent = directoryService.getOrCreateRootFolder(GROUP_ID).getRootDir();
            directoryService.updatePermission(admin, directoryParent.getId(), directoryPermissions);

            Directory directoryChild = directoryService.createFolder(admin, directoryParent.getId(), "Child Folder");

            FileInfo fileInfoParent = FileInfo.builder()
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

            FileInfo fileInfoChild = FileInfo.builder()
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
        };
    }
}
