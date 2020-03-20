package mops;

import mops.persistence.DirectoryPermissionsRepository;
import mops.persistence.DirectoryRepository;
import mops.persistence.FileInfoRepository;
import mops.persistence.directory.Directory;
import mops.persistence.file.FileInfo;
import mops.persistence.permission.DirectoryPermissions;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Arrays;

@Configuration
public class DummyDataSeeding {
    /**
     * @param directoryRepo            connection to the directory table from the database
     * @param fileInfoRepo             connection to the fileInfo table from the database
     * @param directoryPermissionsRepo connection to the directoryPermission table from the database
     * @return an ApplicationRunner
     */
    @Bean
    @Profile("dev")
    public ApplicationRunner init(DirectoryRepository directoryRepo, FileInfoRepository fileInfoRepo,
                                  DirectoryPermissionsRepository directoryPermissionsRepo) {
        return args -> {
            final long groupId = 100L;
            final long fileSize1 = 2_000L;
            final long fileSize2 = 3_000L;
            final String owner1 = "studentin";
            final String owner2 = "studentin1";

            DirectoryPermissions directoryPermissions = DirectoryPermissions.builder()
                    .entry("admin", true, true, true)
                    .entry("editor", true, true, false)
                    .entry("viewer", true, false, false)
                    .entry("editor", true, true, true)
                    .entry("korrektor", true, true, true)
                    .entry("viewer", true, true, true)
                    .entry("studentin", true, true, true)
                    .build();

            directoryPermissions = directoryPermissionsRepo.save(directoryPermissions);

            Directory directoryParent = Directory.builder()
                    .name("")
                    .groupOwner(groupId)
                    .permissions(directoryPermissions)
                    .build();

            directoryParent = directoryRepo.save(directoryParent);

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

            FileInfo fileInfoChild = FileInfo.builder()
                    .name("Test2")
                    .directory(directoryChild)
                    .type("image/png")
                    .size(fileSize2)
                    .owner(owner2)
                    .tag("Test")
                    .build();

            fileInfoRepo.saveAll(Arrays.asList(fileInfoParent, fileInfoChild));
        };
    }
}
