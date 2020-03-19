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

import java.util.Arrays;
import java.util.Optional;

@Configuration
public class DummyDataApplication {


    @Bean
    ApplicationRunner init(DirectoryRepository directoryRepository, FileInfoRepository fileInfoRepository, DirectoryPermissionsRepository directoryPermissionsRepository) {
        return args -> {
            DirectoryPermissions directoryPermissions = DirectoryPermissions.builder()
                    .entry("Admin", true, true, true)
                    .build();


            directoryPermissions = directoryPermissionsRepository.save(directoryPermissions);

            Directory directoryParent = Directory.builder()
                    .name("Root")
                    .groupOwner(100L)
                    .permissions(directoryPermissions.getId())
                    .build();

            directoryParent = directoryRepository.save(directoryParent);

            Directory directoryChild = Directory.builder()
                    .name("Child")
                    .parent(directoryParent.getId())
                    .groupOwner(100L)
                    .permissions(directoryPermissions.getId())
                    .build();

            directoryChild = directoryRepository.save(directoryChild);

            FileInfo fileInfoParent = FileInfo.builder()
                    .name("Test1")
                    .directory(directoryParent.getId())
                    .type("PDF")
                    .size(2000L)
                    .owner("Hebert")
                    .tag("Test")
                    .build();

            FileInfo fileInfoChild = FileInfo.builder()
                    .name("Test2")
                    .directory(directoryChild.getId())
                    .type("PNG")
                    .size(3000L)
                    .owner("MAX")
                    .tag("Test")
                    .build();

            fileInfoRepository.saveAll(Arrays.asList(fileInfoParent, fileInfoChild));

            Optional<DirectoryPermissions> directoryPermissionsFind = directoryPermissionsRepository.findById(directoryPermissions.getId());
            Optional<Directory> directoryFind = directoryRepository.findById(directoryParent.getId());
            Optional<FileInfo> fileInfoFind = fileInfoRepository.findById(fileInfoParent.getId());
            System.out.println(directoryPermissionsFind);
            System.out.println(directoryFind);
            System.out.println(fileInfoFind);


            ;
        };
    }


}
