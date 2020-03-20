package mops.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import mops.DummyDataSeeding;
import mops.persistence.DirectoryPermissionsRepository;
import mops.persistence.DirectoryRepository;
import mops.persistence.FileInfoRepository;
import mops.persistence.directory.Directory;
import mops.persistence.directory.DirectoryBuilder;
import mops.persistence.file.FileInfo;
import mops.persistence.file.FileInfoBuilder;
import mops.persistence.permission.DirectoryPermissions;
import mops.persistence.permission.DirectoryPermissionsBuilder;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static mops.architecture.ArchitectureRuleConfig.*;

@AnalyzeClasses(importOptions = ImportOption.DoNotIncludeTests.class, packages = "mops")
class LayeredArchitectureTest {

    /**
     * This checks, if the layer is correctly used and
     * no wrong accesses are made.
     */
    @ArchTest
    static final ArchRule checkLayeredArchitecture = layeredArchitecture()
            .layer("mopsPersistence").definedBy(MOPS_PERSISTENCE)
            .layer("mopsBusinesslogic").definedBy(MOPS_BUSINESSLOGIC)
            .layer("mopsPresentation").definedBy(MOPS_PRESENTATION)

            .whereLayer("mopsPresentation").mayNotBeAccessedByAnyLayer()
            .whereLayer("mopsBusinesslogic").mayOnlyBeAccessedByLayers("mopsPresentation")
            .whereLayer("mopsPersistence").mayOnlyBeAccessedByLayers("mopsPresentation", "mopsBusinesslogic")

            // This is needed so that we can use DummyData to test the application.
            .ignoreDependency(DummyDataSeeding.class, Directory.class)
            .ignoreDependency(DummyDataSeeding.class, DirectoryRepository.class)
            .ignoreDependency(DummyDataSeeding.class, DirectoryPermissions.class)
            .ignoreDependency(DummyDataSeeding.class, DirectoryPermissionsRepository.class)
            .ignoreDependency(DummyDataSeeding.class, DirectoryPermissionsBuilder.class)
            .ignoreDependency(DummyDataSeeding.class, DirectoryBuilder.class)
            .ignoreDependency(DummyDataSeeding.class, FileInfo.class)
            .ignoreDependency(DummyDataSeeding.class, FileInfoRepository.class)
            .ignoreDependency(DummyDataSeeding.class, FileInfoBuilder.class);

}
