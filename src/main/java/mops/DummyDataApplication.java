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

import java.util.Optional;

@Configuration
public class DummyDataApplication {


    @Bean
    ApplicationRunner init(DirectoryRepository directoryRepository, FileInfoRepository fileInfoRepository, DirectoryPermissionsRepository directoryPermissionsRepository) {
        return args -> {
            DirectoryPermissions directoryPermissions = DirectoryPermissions.builder()
                    .id(1000L)
                    .entry("Admin", true, true, true)
                    .build();


            Directory directoryParent = Directory.builder()
                    .id(1L)
                    .name("Root")
                    .groupOwner(100L)
                    .permissions(directoryPermissions.getId())
                    .build();

            Directory directoryChild = Directory.builder()
                    .id(2L)
                    .name("Root")
                    .parent(directoryParent.getId())
                    .groupOwner(100L)
                    .permissions(directoryPermissions.getId())
                    .build();

            FileInfo fileInfoParent = FileInfo.builder()
                    .id(10L)
                    .name("Test1")
                    .directory(directoryParent.getId())
                    .type("PDF")
                    .size(2000L)
                    .owner("Hebert")
                    .tag("Test")
                    .build();

            FileInfo fileInfoChild = FileInfo.builder()
                    .id(20L)
                    .name("Test2")
                    .directory(directoryChild.getId())
                    .type("PNG")
                    .size(3000L)
                    .owner("MAX")
                    .tag("Test")
                    .build();

            //directoryPermissionsRepository.save(directoryPermissions);
            //directoryRepository.saveAll(Arrays.asList(directoryParent, directoryChild));
            //fileInfoRepository.saveAll(Arrays.asList(fileInfoParent, fileInfoChild));

            Optional<DirectoryPermissions> directoryPermissionsFind = directoryPermissionsRepository.findById(1000L);
            Optional<Directory> directoryFind = directoryRepository.findById(1L);
            Optional<FileInfo> fileInfoFind =  fileInfoRepository.findById(10L);
            System.out.println(directoryPermissionsFind);
            System.out.println(directoryFind);
            System.out.println(fileInfoFind);



            ;};
    }



}
