package mops;

import mops.persistence.directory.Directory;
import mops.persistence.directory.DirectoryRepository;
import mops.persistence.directory.permission.DirectoryPermissionEntry;
import mops.persistence.directory.permission.DirectoryPermissions;
import mops.persistence.directory.permission.DirectoryPermissionsRepository;
import mops.persistence.file.FileInfo;
import mops.persistence.file.FileInfoRepository;
import mops.persistence.file.FileTag;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@SuppressWarnings("PMD")
public class TestService {

    private final FileInfoRepository fiRepo;
    private final DirectoryRepository dirRepo;
    private final DirectoryPermissionsRepository permRepo;

    public TestService(
            FileInfoRepository fiRepo,
            DirectoryRepository dirRepo,
            DirectoryPermissionsRepository permRepo
    ) {
        this.fiRepo = fiRepo;
        this.dirRepo = dirRepo;
        this.permRepo = permRepo;
    }

    public void run() {
        save1();
    }

    private void save1() {
        System.out.println("preDir:" + dirRepo.findAll());
        System.out.println("prePerms:" + permRepo.findAll());
        System.out.println("preFiles:" + fiRepo.findAll());

        DirectoryPermissions perm1 = new DirectoryPermissions(
                Set.of(
                        new DirectoryPermissionEntry("admin", true, true, true)
                )
        );
        System.out.println("perm1: " + perm1);
        DirectoryPermissions perm1AfterSave = permRepo.save(perm1);
        System.out.println("perm1AfterSave: " + perm1AfterSave);

        System.out.println("post-save-perm:" + permRepo.findAll());

        Directory root = new Directory("root", null, 0L, perm1AfterSave.getId());
        System.out.println("root: " + root);
        Directory rootAfterSave = dirRepo.save(root);
        System.out.println("rootAfterSave: " + rootAfterSave);

        System.out.println("post-save-dir:" + dirRepo.findAll());

        FileInfo fi1 = new FileInfo("file1", rootAfterSave.getId(), "text", 0L, "foo",
                Set.of(new FileTag("random"))
        );
        System.out.println("fi1: " + fi1);
        FileInfo fi1AfterSave = fiRepo.save(fi1);
        System.out.println("fi1AfterSave: " + fi1AfterSave);

        System.out.println("post-save-file:" + fiRepo.findAll());

        Optional<FileInfo> loadedFile = fiRepo.findById(fi1AfterSave.getId());
        System.out.println(loadedFile.orElseThrow());

        Optional<Directory> loadedDir = dirRepo.findById(rootAfterSave.getId());
        System.out.println(loadedDir.orElseThrow());

        Optional<DirectoryPermissions> loadedPerm = permRepo.findById(loadedDir.get().getPermissionsId());
        System.out.println(loadedPerm.orElseThrow());
    }
}
