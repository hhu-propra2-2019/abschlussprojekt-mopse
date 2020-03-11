package mops;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTag;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import mops.businesslogic.DirectoryService;
import mops.businesslogic.FileService;
import mops.businesslogic.GroupService;
import mops.persistence.FileRepository;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@ArchTag("checkLayeredArchitecture")
@AnalyzeClasses(packages = "mops")
public class LayeredArchitectureTest {

    private static final String MOPS_PRESENTATION = ArchitectureRuleConfig.MOPS_PRESENTATION;
    private static final String MOPS_BUSINESSLOGIC = ArchitectureRuleConfig.MOPS_BUSINESSLOGIC;
    private static final String MOPS_PERSISTENCE = ArchitectureRuleConfig.MOPS_PERSISTENCE;
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

            /*
             * These dependencies are between the Tests and the different layers,
             * which of course is correct and wanted and should not lead to a failing test.
             */
            .ignoreDependency(Material1ApplicationTests.class, DirectoryService.class)
            .ignoreDependency(Material1ApplicationTests.class, FileRepository.class)
            .ignoreDependency(Material1ApplicationTests.class, FileService.class)
            .ignoreDependency(Material1ApplicationTests.class, GroupService.class)
            .ignoreDependency(SecurityTests.class, DirectoryService.class)
            .ignoreDependency(SecurityTests.class, FileRepository.class)
            .ignoreDependency(SecurityTests.class, FileService.class)
            .ignoreDependency(SecurityTests.class, GroupService.class);
}
