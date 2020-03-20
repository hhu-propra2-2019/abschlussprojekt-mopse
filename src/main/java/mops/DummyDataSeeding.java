package mops;

import mops.businesslogic.Account;
import mops.businesslogic.DirectoryService;
import mops.persistence.DirectoryPermissionsRepository;
import mops.persistence.DirectoryRepository;
import mops.persistence.FileInfoRepository;
import mops.persistence.FileRepository;
import mops.persistence.directory.Directory;
import mops.persistence.file.FileInfo;
import mops.persistence.permission.DirectoryPermissions;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Setups data for development.
 */
@Configuration
public class DummyDataSeeding {

    /**
     * Group id.
     */
    private static final long GROUP_ID = 100L;

    /**
     * @param directoryRepo            connection to the directory table from the database
     * @param fileInfoRepo             connection to the fileInfo table from the database
     * @param fileRepository           connection to the MinIO file repository
     * @param directoryPermissionsRepo connection to the directoryPermission table from the database
     * @param directoryService         directory service
     * @return an ApplicationRunner
     */
    @Bean
    @Profile("dev")
    public ApplicationRunner init(DirectoryRepository directoryRepo,
                                  FileInfoRepository fileInfoRepo,
                                  FileRepository fileRepository,
                                  DirectoryPermissionsRepository directoryPermissionsRepo,
                                  DirectoryService directoryService) {
        return args -> {
            final int fileSize1 = 2_000;
            final int fileSize2 = 3_000;
            final String owner1 = "studentin";
            final String owner2 = "studentin1";

            Account admin = Account.of("admin", "admin@hhu.de", "admin");

            DirectoryPermissions directoryPermissions = DirectoryPermissions.builder()
                    .entry("admin", true, true, true)
                    .entry("editor", true, true, false)
                    .entry("viewer", true, false, false)
                    .entry("editor", true, true, true)
                    .entry("korrektor", true, true, true)
                    .entry("viewer", true, true, true)
                    .entry("studentin", true, true, true)
                    .build();

            Directory directoryParent = directoryService.getOrCreateRootFolder(GROUP_ID);
            directoryService.updatePermission(admin, directoryParent.getId(), directoryPermissions);

            Directory directoryChild = Directory.builder()
                    .fromParent(directoryParent)
                    .name("Child")
                    .build();
            directoryChild = directoryRepo.save(directoryChild);

            FileInfo fileInfoParent = FileInfo.builder()
                    .name("Test1")
                    .directory(directoryParent)
                    .type("application/pdf")
                    .size(fileSize1)
                    .owner(owner1)
                    .tag("Test")
                    .build();
            fileInfoParent = fileInfoRepo.save(fileInfoParent);

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
            fileInfoChild = fileInfoRepo.save(fileInfoChild);

            byte[] contentChild = new byte[fileSize2];
            try (InputStream stream = new ByteArrayInputStream(contentChild)) {
                fileRepository.saveFile(stream, fileSize2, fileInfoChild.getType(), fileInfoChild.getId());
            }
        };
    }
}
