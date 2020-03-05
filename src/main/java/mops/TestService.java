package mops;

import mops.persistence.directory.Directory;
import mops.persistence.directory.DirectoryPermissionEntry;
import mops.persistence.directory.DirectoryPermissions;
import mops.persistence.directory.DirectoryRepository;
import mops.persistence.file.FileInfo;
import mops.persistence.file.FileInfoDO;
import mops.persistence.file.FileInfoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class TestService {

    private final FileInfoRepository fiRepo;
    private final DirectoryRepository dirRepo;

    public TestService(FileInfoRepository fiRepo, DirectoryRepository dirRepo) {
        this.fiRepo = fiRepo;
        this.dirRepo = dirRepo;
    }

    public void run() {
        save1();
        save2();
    }


    private void save1() {
        System.out.println("pre:" + fiRepo.findAll());

        FileInfoDO fi1 = new FileInfoDO("file1", 1L, "text", 0L, "foo");
        System.out.println("fi1: " + fi1);
        FileInfoDO fi1AfterSave = fiRepo.save(fi1);
        System.out.println("fi1AfterSave: " + fi1AfterSave);

        System.out.println("post-save:" + fiRepo.findAll());

        Optional<FileInfoDO> loaded = fiRepo.findById(1L);
        System.out.println(loaded.orElseThrow());
    }

    private void save2() {
        System.out.println("pre:" + dirRepo.findAll());

        Directory root = new Directory("root", 1L, -1L,
                new DirectoryPermissions(
                        Set.of(
                                new DirectoryPermissionEntry("admin", true, true, true)
                        )
                )
        );
        System.out.println("root: " + root);
        Directory rootAfterSave = dirRepo.save(root);
        System.out.println("rootAfterSave: " + rootAfterSave);

        System.out.println("post-save:" + dirRepo.findAll());

        Optional<Directory> loaded = dirRepo.findById(1L);
        System.out.println(loaded.orElseThrow());
    }

    private FileInfo toObj(FileInfoDO fi) {
        return new FileInfo(
                fi.getId(),
                fi.getName(),
                dirRepo.findById(fi.getDirectory()).orElseThrow(),
                fi.getType(),
                fi.getSize(),
                fi.getOwner()
        );
    }
}
